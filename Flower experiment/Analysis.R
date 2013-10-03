#Read in the data from the csv file
detach(data)
data = read.csv("Aggregated batch123 crowd+niche+comments+classification-spam_v7.csv",head=TRUE)
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


# list all (names/corrected name/family) (1,2,3) in one dataframe
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

#Create a data frame containing each print on one row and aggregate info on the columns
prints = unique(subset(data, select=c("Image","Dimension")))
prints = prints[order(prints$Image),]
#add the hash for the Image (such it will fit in a integer vector)
prints$hash = apply(prints,1,function(m) spooky.32(m[1]))
#add nrAnnotations
prints$nrNicheAnnotations = apply(prints,1,function(m) nrow(names[names$Channel=="niche" & names$Image==m[1], ])  )
prints$nrCrowdAnnotations = apply(prints,1,function(m) nrow(names[names$Channel!="niche" & names$Image==m[1], ])  )
prints$nrUniqueNicheAnnotations = apply(prints,1,function(m) nrow(unique(names[names$Channel=="niche" & names$Image==m[1], ]))  )
prints$nrUniqueCrowdAnnotations = apply(prints,1,function(m) nrow(unique(names[names$Channel!="niche" & names$Image==m[1], ]))  )
prints$overlap = apply(prints,1,function(m) length(intersect(names[names$Image==m[1] & names$Channel=="niche",]$name, names[names$Image==m[1] & names$Channel!="niche",]$name)))
prints$overlapPercentage = prints$overlap / prints$nrUniqueNicheAnnotations
prints$confidence = apply(prints,1,function(m) mean(data[Image==m[1],]$AverageConfidence,na.rm=T))
prints$maxCrowdAgreement = apply(prints,1,function(m){
 	x = names[names$Image==m[1] & names$Channel!="niche",]$name 
	y = aggregate(x,by=list(x),FUN=length)
	max(y[2])
	}
)

#Get the prints (hashed image urls) with high (0.66 1.0) overlap
highconfidence = prints[prints$overlapPercentage >0.6 & !is.na(prints$overlapPercentage),]
#Calculates the mean of the confidence scores of the high overlap prints
mean(highconfidence$confidence)

# overlap types (n nicheAnnotation, m crowdAnnotation, o overlap, #occurences)
aggregate.data.frame(prints$overlap,by=list(prints$nrUniqueNicheAnnotations,prints$nrUniqueCrowdAnnotations,prints$overlap),length)

#Average confidence score per dimension
aggregate(prints$confidence,by=list(prints$Dimension),FUN="mean")

#average confidence scores for overlap categories (0,0.33,0.5,0.66,1.0, "rest"=no niche annotations)
aggregate(prints$confidence, by=list("overlapPercentage"=prints$overlapPercentage),FUN="mean")

#Percentage of prints per dimension that have at least n crowd agreement
n = 4
x = table(prints[prints$maxCrowdAgreement>=n,]$Dimension)
x / nrPrints

#crowd agreement
x = names[names$Channel!="niche",]
crowdagreement = aggregate(x$name,by=list("Image" = x$Image,"name" = x$name),FUN=length)
colnames(crowdagreement) = c("Image","name","agreement")

#high agreement annotations
high = 3
x = crowdagreement
y = x[x$agreement >= high,]
z = aggregate(y$name,by=list("name"=y$name),FUN=length)
z[order(z[2],decreasing=T),]


