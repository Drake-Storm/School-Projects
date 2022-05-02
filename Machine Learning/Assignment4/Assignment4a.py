"""
Assignment 4a
This program will read in 4 different data files (using my student number), for each one it will split it into train/test data and labels, then train and test a perceptron algorithm and output the results. From these results it seems that the only file which data is not linearly separable is #3, the rest are linearly separable. This is because the other files get fairly high accuracies while #3 gets a lot lower accuracy score.
Drake Storm Hackett, 000783796, 2021"""

import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score

## Print the results
def toString(theTestLabels, thePredictions, theWeights, theThreshold, theDataFileName):
    """This function returns a string of the final accuracy, weights, and threshold for the testing data.
    theTestLabels = the labels of the testing data
    thePredictions = the predictions from the testing data
    theWeights = the final weights
    theThreshold = the final threshold
    theDataFileName = the name of the file the data was read from
    """
    return theDataFileName + ": " + "{:.2f}".format(accuracy_score(theTestLabels, thePredictions) * 100) + "% W: " + str(theWeights) + " T: " + "{:.2f}".format(theThreshold)

## The Perceptron Learning Algorithm
def trainPerceptron(threshold, weights, learningRate, trainData, trainLabels):
    """This function will train the perceptron
    threshold = the initial threshold
    weights = the initial weights
    learningRate = the learning rate for the algorithm
    trainData = the training data
    tainLabels = the training labels
    """
    stoppingCondition = True
    theAccuracies = [0]
    accuracies = np.array(theAccuracies)
    epoch = 0
    while stoppingCondition:
        predictions = []
        for row in range(len(trainLabels)):
            out = 1 if ((trainData[row] * weights).sum()) > threshold else 0
            predictions.append(out)
            if out < trainLabels[row]:
                for (weight, feature) in zip(weights, trainData[row]):
                    weight = weight + feature * learningRate
                    weights[np.where(trainData[row] == feature)] = weight
                threshold = threshold - learningRate

            if out > trainLabels[row]:
                for (weight, feature) in zip(weights, trainData[row]):
                    weight = weight - feature * learningRate
                    weights[np.where(trainData[row] == feature)] = weight
                threshold = threshold + learningRate

        accuracies = np.append(accuracies, accuracy_score(trainLabels, predictions))
        if len(accuracies) >= 1:
            if accuracies[-1] >= 0.99:  #if the perceptron reached an accuracy of 98% or more
                stoppingCondition = False
        #if the accuracy has not improved in the last 5 epochs
        improved = False
        if len(accuracies) > 5:
            for i in range(1, 6):
                if accuracies[-i] > accuracies[-(i+1)]:
                    improved = True
        if improved:
            stoppingCondition = False
        epoch += 1

    return threshold, weights


## Loop for each data file

for run in range(1, 5):
    filename = "files/000783796_" + str(run) + ".csv"
    rawData = np.loadtxt(filename, dtype=int, delimiter=",")
    labels = rawData[:,-1]
    theData = rawData[:, :-1].astype(float)
    trainData, testData, trainLabels, testLabels = train_test_split(theData, labels, train_size=0.75)
    t = np.random.uniform(-1,1,1)
    initialThreshold = t[0]
    initialWeights = np.random.uniform(-1, 1, len(trainData[0]))
    learningRate = 0.01
    theThreshold, theWeights = trainPerceptron(initialThreshold, initialWeights, learningRate, trainData, trainLabels)
    predictions = []
    for row in range(len(testLabels)):
        out = 1 if ((testData[row] * theWeights).sum()) > theThreshold else 0
        predictions.append(out)

    print(toString(testLabels, predictions, np.around(theWeights, decimals = 2), theThreshold, filename) + "\n")



