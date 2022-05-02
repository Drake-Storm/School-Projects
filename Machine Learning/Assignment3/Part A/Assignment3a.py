"""
Assignment 3a
This program reads data from the Algerian_forest_fires_dataset_UPDATE.csv file, then
Drake Storm Hackett, 000783796, 2021"""

import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
from sklearn.naive_bayes import GaussianNB
from matplotlib import pyplot as plt

##Read data

rawData = np.loadtxt("Algerian_forest_fires_dataset_UPDATE.csv", dtype=str, delimiter=",", skiprows=2)
labels = rawData[:,-1]
theData = rawData[:, :-1].astype(float)
gnb = GaussianNB()


## Split the data and run 50 times

correctProbs = np.zeros(50)
incorrectProbs = np.zeros(50)
accuracies = np.zeros(50)

for run in range(0, 50):
    trainData, testData, trainLabels, testLabels = train_test_split(theData, labels, train_size=0.75)
    gnb.fit(trainData, trainLabels)

    predictions = gnb.predict(testData)
    probabilities = gnb.predict_proba(testData).max(axis=1)
    accuracies[run] = accuracy_score(testLabels, predictions)
    correct = probabilities[np.where(predictions == testLabels)]
    incorrect = probabilities[np.where(predictions != testLabels)]
    correctProbs[run] = np.average(correct)
    incorrectProbs[run] = np.average(incorrect)



## Print results
print("Algerian Forest Fires Data Tests")
print("")
print("Average Accuracy: ", np.average(accuracies))
print("Average probability for correct predictions: ", np.average(correctProbs))
print("Average probability for incorrect predictions: ", np.average(incorrectProbs))

