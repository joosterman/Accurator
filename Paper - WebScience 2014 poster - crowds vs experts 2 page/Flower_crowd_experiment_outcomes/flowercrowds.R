#load data
setwd("<FILEDIR>")
data = read.csv("f383708_with_untrusted_comments.csv",head=TRUE)
prints = read.csv("dataset_complete.csv",head=TRUE)
prints = subset(prints, select=c("image_url","prominence","test_data","nrflowers_lower","nrflowers_upper","nrtypes_lower","nrtypes_upper"))
colnames(prints) = c("image","prominence","test_data","nrflowers_lower","nrflowers_upper","nrtypes_lower","nrtypes_upper")
orig_workers = read.csv("workset383708.csv",head=TRUE)
#load experts and remove prints not used anymore
expertdata = read.csv("expert_labels.csv",TRUE)
expertdata = expertdata[expertdata$Image %in% prints$image,]

#western countries europe and US and canada
westerncountries = c("AND","BEL","FRA","IRL","MCO","NLD","GBR","DEU","CHE","LIE","LUX","AUT","USA","CAN","ITA","ESP","PRT")


###########
#idenitfy workers to be removed and kept
###########
#remove workers with no judgements
workers = orig_workers[orig_workers$judgments_count>0,]
#remove flagged workers
workers = workers[nchar(as.character(workers$flag_reason),type="bytes")==0,]
#remove workers with too many failed test questions
workers = workers[workers$missed_count / workers$golds_count <= 0.6,]
#nice table
workers = subset(workers,select=c("worker_id","judgments_count","missed_count","golds_count","channel","country","trust_overall"))
#Filter data on these selected workers
data = data[data$X_worker_id %in% workers$worker_id,]

#####
# seperate data when a print was used as test question from "real" questions
#####
datatest = data[data$X_golden ,]
data = data[!(data$X_golden) ,]

#######
# Only keep the workers who performed a complete set of tasks (4 real, 1 test) after the quiz
#######
workers$nrtasks = apply(workers,1,function(m) nrow(data[data$X_worker_id==as.integer(m[1]),]))   
workers$nrtaskunable = apply(workers,1,function(m) nrow(data[data$X_worker_id==as.integer(m[1]) & data$other=="unable" ,]))   
workers$nrtaskfantasy = apply(workers,1,function(m) nrow(data[data$X_worker_id==as.integer(m[1]) & data$other=="fantasy" ,]))
workers$nrtaskflower = apply(workers,1,function(m) nrow(data[data$X_worker_id==as.integer(m[1]) & data$other=="" ,]))
#select the workers
workers = workers[workers$nrtasks>4,]
#filter the data
data = data[data$X_worker_id %in% workers$worker_id,]

######
# Archana big ass data merge
######
#x = merge(data,prints, by.x="image_url",by.y="image")
#y = merge(x,workers,by.x="X_worker_id",by.y="worker_id")
#write.csv(y,"all_data.csv")

#make subsets of prints according #flower and #types, easy=0, medium=1-2, hard<2
prints_flower_easy = prints[prints$nrflowers_upper - prints$nrflowers_lower == 0,]
prints_flower_medium = prints[prints$nrflowers_upper - prints$nrflowers_lower == 1 | prints$nrflowers_upper - prints$nrflowers_lower==2,]
prints_flower_hard = prints[prints$nrflowers_upper - prints$nrflowers_lower > 2,]
prints_types_easy = prints[prints$nrtypes_upper - prints$nrtypes_lower == 0,]
prints_types_medium = prints[prints$nrtypes_upper - prints$nrtypes_lower == 1 | prints$nrtypes_upper - prints$nrtypes_lower==2,]
prints_types_hard = prints[prints$nrtypes_upper - prints$nrtypes_lower > 2,]

####
# Format the flower names in an easy to use table (only use data which is not unable or fantasy)
####
#crowd data
d = data[data$other=="",]
x1 = subset(d, select=c("image_url","X_worker_id","name1_specific","confidencename1", "name1_classification" ))
x2 = subset(d, select=c("image_url","X_worker_id","name2_specific","confidencename2","name2_classification" ))
x3 = subset(d, select=c("image_url","X_worker_id","name3_specific","confidencename3","name3_classification" ))
colnames(x1) = c("image","worker","name", "confidence", "classification")
colnames(x2) = c("image","worker","name", "confidence", "classification")
colnames(x3) = c("image","worker","name", "confidence", "classification")
x = rbind(x1,x2,x3)
flowernames = x[!x$name=="" & !is.na(x$name) & !is.na(x$confidence),]
#expert data
d = expertdata
x1 = subset(d, select=c("Image","WorkerID","name1_specific","name1_classification" ))
x2 = subset(d, select=c("Image","WorkerID","name2_specific","name2_classification" ))
x3 = subset(d, select=c("Image","WorkerID","name3_specific","name3_classification" ))
colnames(x1) = c("image","worker","name", "classification")
colnames(x2) = c("image","worker","name", "classification")
colnames(x3) = c("image","worker","name", "classification")
x = rbind(x1,x2,x3)
flowernamesexpert = x[!x$name=="" & !is.na(x$name) ,]

#######
# Add additional data to the data table
######
#Add columns to data that indicates whether the workers succeeded in the our ground truth (#flowers & #types)
#merge data with the print info (every row now contains the print info)
x = merge(data,prints, by.x="image_url",by.y="image")
data$flowers_passed_groundtruth = x$nrflowers >= x$nrflowers_lower & x$nrflowers <= x$nrflowers_upper
data$types_passed_groundtruth = x$nrflowertypes >= x$nrtypes_lower & x$nrflowertypes <= x$nrtypes_upper
#calculate the number of flowers entered by the workers
data$nrflowerlabels = (nchar(as.character(data$name1_corrected)) > 0)*1 + (nchar(as.character(data$name2_corrected)) > 0)*1 + (nchar(as.character(data$name3_corrected)) > 0)*1
data$avgconfidence = rowMeans(data.frame(data$confidencename1,data$confidencename2,data$confidencename3),na.rm=TRUE)
data$western = data$X_country %in% westerncountries

###############
#buildup the prints datatable
###############
#add the prints category
prints$category_flower = "empty"
prints$category_types = "empty"
prints[prints$nrflowers_upper - prints$nrflowers_lower == 0,]$category_flower = 0
prints[prints$nrflowers_upper - prints$nrflowers_lower == 1 | prints$nrflowers_upper - prints$nrflowers_lower==2,]$category_flower=1
prints[prints$nrflowers_upper - prints$nrflowers_lower > 2,]$category_flower=2
prints[prints$nrtypes_upper - prints$nrtypes_lower == 0,]$category_types=0
prints[prints$nrtypes_upper - prints$nrtypes_lower == 1 | prints$nrtypes_upper - prints$nrtypes_lower==2,]$category_types=1
prints[prints$nrtypes_upper - prints$nrtypes_lower > 2,]$category_types=2

#total number of flower labels provided
prints$nrflowerlabels = apply(prints,1,function(m) nrow(flowernames[flowernames$image==m[1],]))
#unique number of flower labels provided
prints$nrflowerlabelsunique = apply(prints,1,function(m) length(unique(flowernames[flowernames$image==m[1],]$name)))

#number of contributors performing the task total and the two groups (flower, noflower)
prints$nrtasks  = apply(prints,1,function(m) nrow(data[data$image_url==m[1],]))
prints$nrtasks_flower = apply(prints,1,function(m) nrow(data[data$image_url==m[1] & data$nrflowerlabels > 0,]))
prints$nrtasks_unable = apply(prints,1,function(m) nrow(data[data$image_url==m[1] & data$other=="unable",]))
prints$nrtasks_fantasy = apply(prints,1,function(m) nrow(data[data$image_url==m[1] & data$other=="fantasy",]))
prints$nrtasks_noflower = prints$nrtasks - prints$nrtasks_flower 
prints$majority_noflower = prints$nrtasks_noflower / prints$nrtasks >= 0.5

#number of workers who passed the groundtruth
prints$nr_passed_flower_groundtruth = apply(prints,1,function(m) nrow(data[data$image_url==m[1] & data$flowers_passed_groundtruth ,]))
prints$fail_rate_flower_groundtruth = 1- (prints$nr_passed_flower_groundtruth / prints$nrtasks)

prints$nr_passed_types_groundtruth = apply(prints,1,function(m) nrow(data[data$image_url==m[1] & data$types_passed_groundtruth ,]))
prints$fail_rate_types_groundtruth = 1- (prints$nr_passed_types_groundtruth / prints$nrtasks)

prints$nr_passed_groundtruth = apply(prints,1,function(m) nrow(data[data$image_url==m[1] & data$flowers_passed_groundtruth & data$types_passed_groundtruth,]))
prints$fail_rate_groundtruth = 1-(prints$nr_passed_groundtruth / prints$nrtasks )

#number of workers who passed the groundtruth split by  workers who provided flowers names and those that didn't (unable/fantasy)
prints$nr_passed_flower_groundtruth_flower = apply(prints,1,function(m) nrow(data[data$image_url==m[1] & data$flowers_passed_groundtruth & data$nrflowerlabels>0 ,]))
prints$fail_rate_flower_groundtruth_flower = 1- (prints$nr_passed_flower_groundtruth_flower / prints$nrtasks_flower)

prints$nr_passed_flower_groundtruth_noflower= apply(prints,1,function(m) nrow(data[data$image_url==m[1] & data$flowers_passed_groundtruth & data$nrflowerlabels==0 ,]))
prints$fail_rate_flower_groundtruth_noflower = 1- (prints$nr_passed_flower_groundtruth_noflower / prints$nrtasks_noflower)

prints$nr_passed_types_groundtruth_flower = apply(prints,1,function(m) nrow(data[data$image_url==m[1] & data$types_passed_groundtruth & data$nrflowerlabels>0 ,]))
prints$fail_rate_types_groundtruth_flower = 1 - (prints$nr_passed_types_groundtruth_flower / prints$nrtasks_flower)

prints$nr_passed_types_groundtruth_noflower = apply(prints,1,function(m) nrow(data[data$image_url==m[1] & data$types_passed_groundtruth & data$nrflowerlabels==0 ,]))
prints$fail_rate_types_groundtruth_noflower = 1- (prints$nr_passed_types_groundtruth_noflower / prints$nrtasks_noflower )

prints$nr_passed_groundtruth_flower = apply(prints,1,function(m) nrow(data[data$image_url==m[1] & data$flowers_passed_groundtruth & data$types_passed_groundtruth & data$nrflowerlabels>0,]))
prints$fail_rate_groundtruth_flower = 1 - (prints$nr_passed_groundtruth_flower / prints$nrtasks_flower )

prints$nr_passed_groundtruth_noflower = apply(prints,1,function(m) nrow(data[data$image_url==m[1] & data$flowers_passed_groundtruth & data$types_passed_groundtruth & data$nrflowerlabels==0,]))
prints$fail_rate_groundtruth_noflower = 1- (prints$nr_passed_groundtruth_noflower / prints$nrtasks_noflower)
#mean and STD of the #flowers and #types of the prints by the crowd
prints$crowd_nrflowers_mean = apply(prints,1,function(m) mean(data[data$image_url==m[1],]$nrflowers,na.rm=TRUE))
prints$crowd_nrflowers_sd = apply(prints,1,function(m) sd(data[data$image_url==m[1],]$nrflowers,na.rm=TRUE))
prints$crowd_nrtypes_mean = apply(prints,1,function(m) mean(data[data$image_url==m[1],]$nrflowertypes,na.rm=TRUE))
prints$crowd_nrtypes_sd = apply(prints,1,function(m) sd(data[data$image_url==m[1],]$nrflowers,na.rm=TRUE))


#average confidence for all the flower labels provided for a print
prints$avgconfidence = apply(prints,1,function(m) mean(flowernames[flowernames$image==m[1],]$confidence))

######
# Further build up the worker datatable (IMPORTANT compare the m[1] as integer, else it's a string!!!!)
######
workers$nrflowers = apply(workers,1,function(m) nrow(flowernames[flowernames$worker==as.integer(m[1]) ,]))   
workers$nrflowersunique = apply(workers,1,function(m) length( unique(flowernames[flowernames$worker==as.integer(m[1]) ,]$name)  ))
workers$avgconfidence = apply(workers,1,function(m)  mean(flowernames[flowernames$worker==as.integer(m[1]) ,]$confidence)  )
workers$nr_passed_flower_groundtruth = apply(workers,1,function(m) nrow(data[data$X_worker_id==as.integer(m[1]) & data$flowers_passed_groundtruth,]))
workers$nr_passed_types_groundtruth = apply(workers,1,function(m) nrow(data[data$X_worker_id==as.integer(m[1]) & data$types_passed_groundtruth,]))
workers$nr_passed_groundtruth = apply(workers,1,function(m) nrow(data[data$X_worker_id==as.integer(m[1]) & data$flowers_passed_groundtruth & data$types_passed_groundtruth,]))
workers$highquality = workers$nr_passed_groundtruth / workers$nrtasks > median(workers$nr_passed_groundtruth / workers$nrtasks)
workers$avgtotaltime = apply(workers,1,function(m) mean(data[data$X_worker_id==as.integer(m[1]) ,]$totaltime,na.rm=T)  /1000  )


#########
#Analysis
########

#ANOVA test for prominence fail_rate groundtruth
r = aov(fail_rate_flower_groundtruth ~ prominence , data=prints)
summary(r)
r = aov(fail_rate_types_groundtruth ~ prominence, data=prints)
summary(r)

#What is the avg confidence of the second and third flower annotation for workers who provided them compared to the first
nrow( data[data$nrflowerlabels==1,])
nrow( data[data$nrflowerlabels==2,])
nrow( data[data$nrflowerlabels==3,])
#only for two provided labels
twolabels = data[data$nrflowerlabels==2,]
mean(twolabels$confidencename1,na.rm=TRUE)
sd(twolabels$confidencename1,na.rm=TRUE)
mean(twolabels$confidencename2,na.rm=TRUE)
sd(twolabels$confidencename2,na.rm=TRUE)
#only for three
threelabels = data[data$nrflowerlabels==3,]
mean(threelabels$confidencename1,na.rm=TRUE)
sd(threelabels$confidencename1,na.rm=TRUE)
mean(threelabels$confidencename2,na.rm=TRUE)
sd(threelabels$confidencename2,na.rm=TRUE)
mean(threelabels$confidencename3,na.rm=TRUE)
sd(threelabels$confidencename3,na.rm=TRUE)

#Do workers who provide a usefull reference show more confident (or provide more labels)?
#Usefull references are U (web reference) and B (book title) 
#tasks with labels
nrow(data[data$nrflowerlabels>0 ,])
ref = data[data$nrflowerlabels > 0 & (data$reference_classification=="U" | data$reference_classification=="B"),]
nrow(ref)
noref = data[data$nrflowerlabels > 0 & !(data$reference_classification=="U" | data$reference_classification=="B"),]
nrow(noref)
#ref
mean(ref$nrflowerlabels)
sd(ref$nrflowerlabels)
mean(ref$confidencename1,na.rm=TRUE)
mean(ref$confidencename2,na.rm=TRUE)
mean(ref$confidencename3,na.rm=TRUE)
#noref
mean(noref$nrflowerlabels)
sd(noref$nrflowerlabels)
mean(noref$confidencename1,na.rm=TRUE)
mean(noref$confidencename2,na.rm=TRUE)
mean(noref$confidencename3,na.rm=TRUE)

#distribution of prominence across easy medium hard prints (#flowers)
nrow(prints_flower_easy[prints_flower_easy$prominence,])
nrow(prints_flower_easy[!prints_flower_easy$prominence,])
nrow(prints_flower_medium[prints_flower_medium$prominence,])
nrow(prints_flower_medium[!prints_flower_medium$prominence,])
nrow(prints_flower_hard[prints_flower_hard$prominence,])
nrow(prints_flower_hard[!prints_flower_hard$prominence,])
#distribution of prominence across easy medium hard prints (#types)
nrow(prints_types_easy[prints_types_easy$prominence,])
nrow(prints_types_easy[!prints_types_easy$prominence,])
nrow(prints_types_medium[prints_types_medium$prominence,])
nrow(prints_types_medium[!prints_types_medium$prominence,])
nrow(prints_types_hard[prints_types_hard$prominence,])
nrow(prints_types_hard[!prints_types_hard$prominence,])

###########
# Label taxonomy
###########
x= merge(flowernames,prints,by.x="image",by.y="image")
tax = aggregate(x$classification,by=list("category_flower"=x$category_flower,"category_types" = x$category_types),function(m) c("SB" = length(m[m==1]),"SC"=length(m[m==2]),"GB"=length(m[m==3]),"GC"=length(m[m==4]),"FB"=length(m[m==5]),"FC"=length(m[m==6])))


##########
# Distribution of flower names + avg confidence
#########
dist_flowers = aggregate(flowernames$name,by=list("name" = flowernames$name),length)
dist_flowers_conf = aggregate(flowernames$confidence,by=list("name"=flowernames$name),mean)
dist = merge(dist_flowers,dist_flowers_conf,by.x="name",by.y="name")
colnames(dist) = c("name","distribution","avg_confidence")
#get the unique print - label pairs
x = unique(subset(flowernames,select=c("image","name"))) 
#aggregate for how many prints a label has been provided
y = aggregate(x$image,by=list("name"=x$name),length)
#merge the usedin_nrprint
labeldistribution = merge(dist,y, by.x="name",by.y="name")
colnames(labeldistribution) = c("name","distribution","avg_confidence","usedin_nrprints")


#########
#worker and expert agreement
########
#agreement between workers (full matrix)
x = apply(prints,1,function(m) table(flowernames[flowernames$image==m[1],]$name) )
x = as.data.frame(x)
#prints label counts 
x = aggregate(flowernames$name, by=list("image" = flowernames$image,"name" = flowernames$name,"classification"=flowernames$classification), length)
counts = merge(x,prints, x.by="image", y.by="image")
counts = subset(counts, select=c("image","name","x","nrtasks","nrtasks_flower","prominence","classification"))
colnames(counts) =  c("image","name","agreement","nrtasks","nrtasks_flower","prominence","classification")
counts$providedbyexpert = apply(counts,1,function(m) nrow(flowernamesexpert[flowernamesexpert$image ==m[1] & flowernamesexpert$name ==m[2],])>0)
#create majority column
counts$majority = counts$agreement / counts$nrtasks_flower >=0.5
#distinct print-name pairs provided by crowd but not by experts
x = counts[!counts$providedbyexpert,]
nrow(unique(data.frame(x$image,x$name))) 

######
# distinct print-name pairs provided by experts but not by crowds: 
######
y = aggregate(flowernamesexpert$name, by=list("image" = flowernamesexpert$image,"name" = flowernamesexpert$name,"classification"=flowernamesexpert$classification), length)
countsexperts = merge(y,prints, x.by="image", y.by="image")
countsexperts = subset(countsexperts, select=c("image","name","x","nrtasks","nrtasks_flower","prominence","classification"))
colnames(countsexperts) =  c("image","name","agreement","nrtasks","nrtasks_flower","prominence","classification")
countsexperts$providedbycrowd = apply(countsexperts,1,function(m) nrow(flowernames[flowernames$image ==m[1] & flowernames$name ==m[2],])>0)
#distinct print-name pairs provided by experts but not by crowds
x = countsexperts[!countsexperts$providedbycrowd,]
nrow(unique(data.frame(x$image,x$name)))

#number of prints for which there is at least one majority label
length(unique(counts[counts$majority,]$image))
#number of prints-labels with crowd majority
nrow(counts[counts$majority,])
#number of prints-labels with crowd majority and agreement of experts
x = counts[counts$majority & counts$providedbyexpert,]
y = merge(x,prints,by.x="image",by.y="image")
majority = subset(y,select=c("image","name","nrtasks.x","prominence.x","category_flower","category_types","majority","providedbyexpert"))
#create majority count column on the prints
prints$nrmajority_labels = apply(prints,1,function(m) nrow(counts[counts$majority & counts$image==m[1],]))

#Worker demographics
#western data
data_western = data[data$X_country %in% westerncountries,]
nrow(data_western)
#western workers
workers_western = workers[workers$country %in% westerncountries,]
nrow(workers_western)
#non-western data
data_eastern = data[!(data$X_country %in% westerncountries),]
nrow(data_eastern)
#non-western workers
workers_eastern = workers[!(workers$country %in% westerncountries),]
nrow(workers_eastern)

#prints stats split per worker region western - non-wester
#western
prints_western = prints
prints_western$western = TRUE
prints_western$nrtasks = apply(prints,1,function(m) nrow(data_western[data_western$image_url==m[1],]))
prints_western$nr_passed_flower_groundtruth = apply(prints,1,function(m) nrow(data_western[data_western$image_url==m[1] & data_western$flowers_passed_groundtruth ,]))
prints_western$nr_passed_types_groundtruth = apply(prints,1,function(m) nrow(data_western[data_western$image_url==m[1] & data_western$types_passed_groundtruth ,]))
prints_western$nr_passed_groundtruth = apply(prints,1,function(m) nrow(data_western[data_western$image_url==m[1] & data_western$flowers_passed_groundtruth & data_western$types_passed_groundtruth,]))
tab_prints_western = subset(prints_western, select=c("image","western","category_flower","category_types","prominence","nrtasks","nr_passed_flower_groundtruth","nr_passed_types_groundtruth","nr_passed_groundtruth"))
#non-western
prints_eastern = prints
prints_eastern$western = FALSE
prints_eastern$nrtasks = apply(prints,1,function(m) nrow(data_eastern[data_eastern$image_url==m[1],]))
prints_eastern$nr_passed_flower_groundtruth = apply(prints,1,function(m) nrow(data_eastern[data_eastern$image_url==m[1] & data_eastern$flowers_passed_groundtruth ,]))
prints_eastern$nr_passed_types_groundtruth = apply(prints,1,function(m) nrow(data_eastern[data_eastern$image_url==m[1] & data_eastern$types_passed_groundtruth ,]))
prints_eastern$nr_passed_groundtruth = apply(prints,1,function(m) nrow(data_eastern[data_eastern$image_url==m[1] & data_eastern$flowers_passed_groundtruth & data_eastern$types_passed_groundtruth,]))
tab_prints_eastern = subset(prints_eastern, select=c("image","western","category_flower","category_types","prominence","nrtasks","nr_passed_flower_groundtruth","nr_passed_types_groundtruth","nr_passed_groundtruth"))
tab_prints_region_split = rbind(tab_prints_western,tab_prints_eastern)

#western / eastern correctness (not significant)
mean(workers_western$nr_passed_flower_groundtruth)
mean(workers_eastern$nr_passed_flower_groundtruth)
wilcox.test(workers_western$nr_passed_flower_groundtruth,workers_eastern$nr_passed_flower_groundtruth)
mean(workers_western$nr_passed_types_groundtruth)
mean(workers_eastern$nr_passed_types_groundtruth)
wilcox.test(workers_western$nr_passed_types_groundtruth,workers_eastern$nr_passed_types_groundtruth)
mean(workers_western$nr_passed_groundtruth)
mean(workers_eastern$nr_passed_groundtruth)
wilcox.test(workers_western$nr_passed_groundtruth,workers_eastern$nr_passed_groundtruth)

#western / eastern nrtasks (not significant)
wilcox.test(workers_western$nrtasks,workers_eastern$nrtasks)
#western / eastern totaltime (significant)
wilcox.test(data_western$totaltime,data_eastern$totaltime)
#western / eastern avg confidence (nog significant)
wilcox.test(data_western$avgconfidence,data_eastern$avgconfidence)
#Good / bad reference confidence (not significant)
wilcox.test(data[data$nrflowerlabels >0 & data$reference_classification=="U",]$confidencename1,data[data$nrflowerlabels >0 & !data$reference_classification=="U",]$confidencename1)


#######
# Comments reference
######
table(data$reference_classification)
#comments per worker
table(data_western$reference_classification) / 125
table(data_eastern$reference_classification) / 225
#commment per print type
x = merge(data,prints,by.x="image_url",by.y="image")
table(x[x$category_flower==0,]$reference_classification) / (160)
table(x[x$category_flower==1,]$reference_classification) / (98)
table(x[x$category_flower==2,]$reference_classification) /(56)

table(x[x$category_types==0,]$reference_classification) / (193)
table(x[x$category_types==1,]$reference_classification) / (125)
table(x[x$category_types==2,]$reference_classification) / (32)

#######
# Comments unable
######
table(data$unable_classification)
#comments per worker
table(data_western$unable_classification) / 17
table(data_eastern$unable_classification) / 55
#commment per print type
x = merge(data,prints,by.x="image_url",by.y="image")
table(x[x$category_flower==0,]$unable_classification) / 30
table(x[x$category_flower==1,]$unable_classification) / 23
table(x[x$category_flower==2,]$unable_classification) / 19

table(x[x$category_types==0,]$unable_classification) / 42
table(x[x$category_types==1,]$unable_classification) / 18
table(x[x$category_types==2,]$unable_classification) / 12

#######
# Comments fantasy
######
table(data$fantasy_classification)
#comments per worker
table(data_western$fantasy_classification) /26
table(data_eastern$fantasy_classification) / 34
#commment per print type
x = merge(data,prints,by.x="image_url",by.y="image")
table(x[x$category_flower==0,]$fantasy_classification) / 34
table(x[x$category_flower==1,]$fantasy_classification) / 12
table(x[x$category_flower==2,]$fantasy_classification) / 14

table(x[x$category_types==0,]$fantasy_classification) / 34
table(x[x$category_types==1,]$fantasy_classification) / 15
table(x[x$category_types==2,]$fantasy_classification) / 11



#number of flower label type per Prominence CROWD and EXPERT
#CROWD
x = merge(flowernames,prints, by.x="image",by.y="image")
#prominent
table(x[x$prominence,]$classification)
#non-prominent
table(x[!x$prominence,]$classification)
#EXPERT
x = merge(flowernamesexpert,prints, by.x="image",by.y="image")
table(x[x$prominence,]$classification)
table(x[!x$prominence,]$classification)

############
# Write out all the result files
############
write.csv(labeldistribution,"flowerlabel_distribution.csv",row.names=FALSE)
write.csv(tax,"taxnomymapping_perdimension.csv",row.names=FALSE)
write.csv(counts,"print-labels.csv",row.names=FALSE)
write.csv(prints,"prints.csv")
write.csv(tab_prints_region_split,"prints_western_nonwestern.csv",row.names=FALSE)
write.csv(workers,"workers.csv")