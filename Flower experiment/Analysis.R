# Packages needed:
# install.packages("hashFunction")

#Read in the data from the csv file
#detach(data)
data = read.csv("Aggregated batch123 crowd+niche+comments+classification-spam_v9.csv",head=TRUE)
attach(data)


#make data frame with executions on only specific prints
sp = data[Dimension.amount=="single" & Dimension.prominence=="prominent" ,]
snp = data[Dimension.amount=="single" & Dimension.prominence=="non-prominent" ,]
mp = data[Dimension.amount=="multiple" & Dimension.prominence=="prominent" ,]
mnp = data[Dimension.amount=="multiple" & Dimension.prominence=="non-prominent" ,]

#make data frames for each channel and an aggregate for the crowd (c5)
c1 = data[Channel=="niche",]
c2 = data[Channel=="amt",]
c3 = data[Channel=="vivatic",]
c4 = data[Channel=="point_dollars",]
c5 = data[Channel!="niche",]

#Single+Prominent per channel
spc1 = sp[sp$Channel=="niche",]
spc2 = sp[sp$Channel=="amt",]
spc3 = sp[sp$Channel=="vivatic",]
spc4 = sp[sp$Channel=="point_dollars",]
spc5 = sp[sp$Channel!="niche",]

#Single+Non-Prominent per channel
snpc1 = snp[snp$Channel=="niche",]
snpc2 = snp[snp$Channel=="amt",]
snpc3 = snp[snp$Channel=="vivatic",]
snpc4 = snp[snp$Channel=="point_dollars",]
snpc5 = snp[snp$Channel!="niche",]

#Multiple+Prominent per channel
mpc1 = mp[mp$Channel=="niche",]
mpc2 = mp[mp$Channel=="amt",]
mpc3 = mp[mp$Channel=="vivatic",]
mpc4 = mp[mp$Channel=="point_dollars",]
mpc5 = mp[mp$Channel!="niche",]

#Multiple+Non-Prominent per channel
mnpc1 = mnp[mnp$Channel=="niche",]
mnpc2 = mnp[mnp$Channel=="amt",]
mnpc3 = mnp[mnp$Channel=="vivatic",]
mnpc4 = mnp[mnp$Channel=="point_dollars",]
mnpc5 = mnp[mnp$Channel!="niche",]

#store the number of prints per dimension
nrPrints = c("d1"=8, "d2"=16,"d3"=9,"d4"=53)

# list all (nname1botanical) (1,2,3) in one dataframe. Contains only flower names, non flower names are removed.
x1 = subset(data, select=c(Image,Channel,name1botanical))
x2 = subset(data, select=c(Image,Channel,name2botanical))
x3 = subset(data, select=c(Image,Channel,name3botanical))
colnames(x1) = c("Image","Channel","name")
colnames(x2) = c("Image","Channel","name")
colnames(x3) = c("Image","Channel","name")
x = rbind(x1,x2,x3)
#remove all non flower names
x = x[x$name!="no flower name",]
#remove all empty rows
names = x[nchar(as.character(x$name))>0,]

#background of "no flower name" annotations
x1 = data[data$name1corrected=="no flower name",]$name1
x2 = data[data$name2corrected=="no flower name",]$name2
x3 = data[data$name3corrected=="no flower name",]$name3
x = table(as.character(x1,x2,x3))
noflowers = x[order(x,decreasing=T)]

#Create a data frame containing each print on one row and aggregate info on the columns (Reminder: annotations are flower name annotations)
prints = unique(subset(data, select=c("Image","Dimension")))
prints = prints[order(prints$Image),]
#add the hash for the Image (such it will fit in a integer vector)
prints$hash = apply(prints,1,function(m) spooky.32(m[1]))
#add nrAnnotations
prints$nrNicheAnnotations = apply(prints,1,function(m) nrow(names[names$Channel=="niche" & names$Image==m[1], ])  )
prints$nrCrowdAnnotations = apply(prints,1,function(m) nrow(names[names$Channel!="niche" & names$Image==m[1], ])  )
prints$nrAnnotations = prints$nrNicheAnnotations+prints$nrCrowdAnnotations
prints$nrUniqueNicheAnnotations = apply(prints,1,function(m) nrow(unique(names[names$Channel=="niche" & names$Image==m[1], ]))  )
prints$nrUniqueCrowdAnnotations = apply(prints,1,function(m) nrow(unique(names[names$Channel!="niche" & names$Image==m[1], ]))  )
prints$nrUniqueAnnotations = apply(prints,1,function(m) nrow(unique(names[names$Image==m[1], ]))  )
prints$overlap = apply(prints,1,function(m) length(intersect(names[names$Image==m[1] & names$Channel=="niche",]$name, names[names$Image==m[1] & names$Channel!="niche",]$name)))
prints$overlapPercentage = prints$overlap / prints$nrUniqueNicheAnnotations
#This is not correct! The meaning of NaN is different from 0. NaN means no niche annotations where 0 means no overlap. NaN is also needed as a seperate category in the analysis. If you want another measure, please make another column, instead of removing/renaming an existing one.
	prints$recall = prints$overlap / prints$nrUniqueNicheAnnotations
	#remove NaN from recall column
	prints$recall[is.nan(prints$recall)] <- 0
prints$precision = prints$overlap / prints$nrUniqueCrowdAnnotations
prints$confidence = apply(prints,1,function(m) mean(data[Image==m[1],]$AverageConfidence,na.rm=T))
prints$executionTime = apply(prints,1,function(m) mean(data[Image==m[1],]$TotalTime,na.rm=T))
prints$maxCrowdAgreement = apply(prints,1,function(m){
 	x = names[names$Image==m[1] & names$Channel!="niche",]$name 
	y = aggregate(x,by=list(x),FUN=length)
	max(y[2])
	}
)
prints$nrNicheWorkers = apply(prints,1,function(m) length(unique(data[Image==m[1] & Channel=="niche",]$WorkerID )))
prints$nrCrowdWorkers = apply(prints,1,function(m) length(unique(data[Image==m[1] & Channel!="niche",]$WorkerID )))
prints$nrWorkers = prints$nrCrowdWorkers + prints$nrNicheWorkers
prints$avgAnnotations= prints$nrAnnotations / prints$nrWorkers 

#Get the prints (hashed image urls) with high (0.66 1.0) overlap
highconfidence = prints[prints$recall >0.6 & !is.na(prints$recall),]
#Calculates the mean of the confidence scores of the high overlap prints
mean(highconfidence$confidence)

# overlap types (n nicheAnnotation, m crowdAnnotation, o overlap, #occurences)
aggregate.data.frame(prints$overlap,by=list(prints$nrUniqueNicheAnnotations,prints$nrUniqueCrowdAnnotations,prints$overlap),length)

#Average confidence score per dimension
aggregate(prints$confidence,by=list(prints$Dimension),FUN="mean")

#average confidence scores for overlap categories (0,0.33,0.5,1.0, "rest"=no niche annotations)
aggregate(prints$confidence, by=list("recall"=prints$recall),FUN="mean")

#Percentage of prints per dimension that have at least n crowd agreement
n = 4
x = table(prints[prints$maxCrowdAgreement>=n,]$Dimension)
x / nrPrints

#crowd agreement
x = names[names$Channel!="niche",]
crowdagreement = aggregate(x$name,by=list("Image" = x$Image,"name" = x$name),FUN=length)
colnames(crowdagreement) = c("Image","name","agreement")

#annotations that have a high(>=2) agreement by crowd workers
high = 2
x = crowdagreement
y = x[x$agreement >= high,]
z = aggregate(y$name,by=list("name"=y$name),FUN=length)
z[order(z[2],decreasing=T),]

#list niche annotations where crowd agreement is high for fantasy annotation
high = 2 
x = crowdagreement
y = x[x$agreement >= high & x$name=="fantasy",]
z = merge(names,y,by.x="Image",by.y="Image")
a = z[z$Channel=="niche",]
aggregate(a$name.x,by=list(a$Image),as.character)

#Images for which at least one niche/crowd provided fantasy
x = names[names$Channel=="niche" & names$name=="fantasy",]
nichefantasy = unique(x$Image)
length(nichefantasy)
y = names[names$Channel!="niche" & names$name=="fantasy",]
crowdfantasy = unique(y$Image)
length(crowdfantasy)
#Images for which both crowd and niche provided fantasy
fantasy = intersect(nichefantasy,crowdfantasy)
#crowd agreement for fantasy prints
z = crowdagreement[crowdagreement$name=="fantasy",]
merge(as.data.frame(fantasy),z,by.x="fantasy",by.y="Image")

#data frame with data based on the percentage of overlap between niche and crowd
z = prints$nrAnnotations / prints$nrWorkers
method = mean
#aggregate per overlap
aggregate(z,by=list("overlapPercentage" = prints$overlapPercentage),FUN=method)
# aggregate per overlap per dimension
x = aggregate(z, by=list("overlapPercentage" = prints$overlapPercentage,"Dimension" = prints$Dimension),FUN=method)
x[order(x$overlapPercentage,na.last=F),]
#aggregate per dimension
aggregate(z, by=list("Dimension" = prints$Dimension),FUN=method)
#total 
mean(z)

#prints that have on average more crowd annotations than niche annotations
averageAnnotations = as.data.frame(cbind("Dimension"=prints$Dimension, "avgAnnPerNicheWorker" = prints$nrNicheAnnotations / prints$nrNicheWorkers, "avgAnnPerCrowdWorker"=prints$nrCrowdAnnotations / prints$nrCrowdWorkers))
#exclude prints with zero annotations for the niche
x = averageAnnotations[averageAnnotations$avgAnnPerNicheWorker>0,]
aggregate(x$avgAnnPerNicheWorker,by=list(x$Dimension),mean)
#crowd with zero niche annotations excluded
aggregate(x$avgAnnPerCrowdWorker,by=list(x$Dimension),mean)
#crowd with zero niche annotations included
aggregate(averageAnnotations$avgAnnPerCrowdWorker,by=list(averageAnnotations$Dimension),mean)
#prints with big (>1) difference between niche and crowd
y = averageAnnotations$avgAnnPerNicheWorker - averageAnnotations$avgAnnPerCrowdWorker
z = prints[(y<= -1 | y>=1) & prints$nrNicheAnnotations>0,]

#calculate the agreement between annotators for each print annotation
x = subset(names, select=c("Image","name"))
y = aggregate(x$name,by=list(x$Image,x$name),length)
colnames(y) = c("Image", "name","count")
z = merge(y,prints,x.by="Image",y.by="Image")
z = subset(z,select=c("Image","Dimension","name","nrWorkers","count"))
z$agreement = z$count / z$nrWorkers
#average the agreement per print
agreementPerPrint = aggregate(z$agreement, by=list(z$Image,z$Dimension),mean)
colnames(agreementPerPrint) = c("Image","Dimension","agreement")
agreementPerDimension = aggregate(agreementPerPrint$agreement, by=list(agreementPerPrint$Dimension),mean)

#comments
dim = "d1"
x = data[data$Dimension==dim,]
#total comments
nrow(x[nchar(as.character(x$ComClD))>0,])
#comments indicating "unable"
nrow(x[x$ComClD == "N" | x$ComClD=="U",]) / nrow(x)
#comments indicating "lack of knowledge"
nrow(x[x$ComClD == "D",]) / nrow(x)

#crowd agreement on annotations not provided by the niche
x = crowdagreement
#get the annotations that are not (also) provided by the niche
y = apply(x,1, function(m) nrow(names[names$Image==m[1] & names$name==m[2] & names$Channel=="niche",])>0)
#filter the agreement dataframe

