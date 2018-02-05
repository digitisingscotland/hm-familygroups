
sigmal <- read.table('/Users/al/Desktop/cora_Generic-Sigma-Levenshtein.tsv',header=T, col.names=c("id1","id2", "distance","match"))
jaccard <- read.table('/Users/al/Desktop/cora_Generic-Average-Jaccard.tsv',header=T, col.names=c("id1","id2", "distance","match"))
averageGLD <- read.table('/Users/al/Desktop/cora_Generic-Average-GLD.tsv',header=T, col.names=c("id1","id2", "distance","match"))
ned2 <- read.table('/Users/al/Desktop/cora_Generic-Average-NED2.tsv',header=T, col.names=c("id1","id2", "distance","match"))

summary(sigmal)
summary(jaccard)
summary(averageGLD)
summary(ned2)

createPlotForNormalisedData <- function( data, normalised, firstplot, ylimit, xlimit, colour , renormalise = FALSE) {

  if( !renormalise ) {
    if( normalised ) {
      data$desc <- round(data$distance,digits=3)
    } else {
      data$desc <- round(data$distance/max(data$distance),digits=3)
    }
  }
  
  counts <- aggregate(data,by=list(data$desc),FUN=length)
  
  counts$desc <- counts$desc / max(counts$desc)
  
  if( firstplot ) {
    plot(counts$Group.1, counts$desc, type="l", xlim=c(0,xlimit), ylim=c(0,ylimit), xlab="Distribution of Cora distances", col=colour)
  } else {
    points(counts$Group.1, counts$desc, type="l", col=colour)
  }
}

plotMatches <- function( data, normalised, firstplot, ylimit, colour, is_match ) {
  
  if( normalised ) {
    data$desc <- round(data$distance,digits=3)
  } else {
    data$desc <- round(data$distance/max(data$distance),digits=3)
  }
  
  xlimit <- max(data$desc)
  
  if(xlimit < 1) {
    xlimit <- 1
  }
  
  data <- data[which(data$match==is_match),]
  
  createPlotForNormalisedData( data, TRUE, firstplot, ylimit, xlimit, colour , renormalise = TRUE)
}

############### Below here is the stuff to run, above here is machinery ############### 

plotMatchesAndNonMaches <- function( data, normalised, firstplot, ylimit, colour ) {
  plotMatches( data, normalised, firstplot, ylimit, colour, "true")
  plotMatches( data, normalised, FALSE, ylimit, colour+1, "false")
}

plotAllMatchesAndNonMatches <- function(height= 16000) {
  plotMatchesAndNonMaches( sigmal, FALSE, TRUE, height, 1)
  plotMatchesAndNonMaches( jaccard, TRUE, FALSE, height, 3)
  plotMatchesAndNonMaches( averageGLD, TRUE, FALSE, height, 5)
  plotMatchesAndNonMaches( ned2, TRUE, FALSE, height, 7)
  
  legend( "topright", c( "SigmaL match", "SigmaL non", "Jaccard match", "Jaccard non", "AverageGLD match", "AverageGLD non", "NED2 match", "NED2 non"),col=c(1,2,3,4,5,6,7,8),lty=c(1,1,1,1,1,1,1,1))
}
  
plotAllDistributions <- function(height= 16000) {
  createPlotForNormalisedData( sigmal, FALSE, TRUE, height, 1)
  createPlotForNormalisedData( jaccard, TRUE, FALSE, height, 2)
  createPlotForNormalisedData( averageGLD, TRUE, FALSE, height, 3)
  createPlotForNormalisedData( ned2, TRUE, FALSE, height, 4)
  
  legend( "topright", c( "SigmaL", "Jaccard", "AverageGLD", "NED2"),col=c(1,2,3,4),lty=c(1,1,1,1))
}



