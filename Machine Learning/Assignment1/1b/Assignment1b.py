"""
Assignment 1b
This program reads data from the Example_WearableComputing_weight_lifting_exercises_biceps_curl_variations.csv file
then randomizes the order and puts them into different arrays including
training/testing data and labels with 75% of the data in the training.
Then it displays the min, max, mean, and medians of each feature of the data.
Then it graphs the training data based on which subjects(labels) the data belongs to.
Drake Storm Hackett, 000783796
"""

import numpy as np
import csv
from matplotlib import pyplot as plt

##Open and read the file
file = open("Example_WearableComputing_weight_lifting_exercises_biceps_curl_variations.csv", "r")
reader = csv.reader(file, delimiter = ",")
featureNames = np.array(next(reader))
dataList = []
for row in reader:
    dataList.append(row)
allData = np.array(dataList)
allData[allData==''] = 0            #This is to stop any errors converting empty strings
allData[allData=='#DIV/0!'] = 0     #This is to stop any conversion errors from the csv file
labels = allData[:,0]
data = allData[:,1:].astype(np.double)

##Shuffle the data and split into training and testing data
randomIndex = np.arange(labels.shape[0])
np.random.shuffle(randomIndex)
labels = labels[randomIndex]
data = data[randomIndex]
#split them 75% training, 25% testing
trainingLabels, testingLabels = np.array_split(labels, [int(0.75 * len(labels))])
trainingData, testingData = np.array_split(data, [int(0.75 * len(data))])

##Print the summary
print("Summary of the Weight Lifting Exercises monitored with Inertial Measurement Units Data Set")
print("Drake Storm Hackett, COMP10200, 2021")

print("TRAINING SET")
print("Features: ", featureNames[1:] )
print("Minima: ", trainingData.min(axis=0))
print("Maxima: ", trainingData.max(axis=0))
print("Means: ", trainingData.mean(axis=0))
print("Medians: ", np.median(trainingData, axis=0))

print("TESTING SET")
print("Features: ", featureNames[1:] )
print("Minima: ", testingData.min(axis=0))
print("Maxima: ", testingData.max(axis=0))
print("Means: ", testingData.mean(axis=0))
print("Medians: ", np.median(testingData, axis=0))


##Graph it

#separate the trainingdata into the labels
trainingData_eurico = trainingData[trainingLabels == "eurico"]
trainingData_adelmo = trainingData[trainingLabels == "adelmo"]
trainingData_carlitos = trainingData[trainingLabels == "carlitos"]
trainingData_pedro = trainingData[trainingLabels == "pedro"]
trainingData_jeremy = trainingData[trainingLabels == "jeremy"]

#Scatter plot timestamps
plt.figure(1)
plt.title("Raw Timestamps graph")
plt.xlabel("raw_timestamp_part_1")
plt.ylabel("raw_timestamp_part_2")
plt.scatter(trainingData_eurico[:,0], trainingData_eurico[:,1], c="red", marker="o")
plt.scatter(trainingData_adelmo[:,0], trainingData_adelmo[:,1], c="blue", marker="v")
plt.scatter(trainingData_carlitos[:,0], trainingData_carlitos[:,1], c="purple", marker="*")
plt.scatter(trainingData_pedro[:,0], trainingData_pedro[:,1], c="cyan", marker="H")
plt.scatter(trainingData_jeremy[:,0], trainingData_jeremy[:,1], c="green", marker="D")
plt.show()

#Scatter plot accel workouts
plt.figure(2)
plt.title("Total Accel Graph")
plt.xlabel("total_accel_belt")
plt.ylabel("total_accel_dumbbell")
plt.scatter(trainingData_eurico[:,6], trainingData_eurico[:,97], c="red", marker="o")
plt.scatter(trainingData_adelmo[:,6], trainingData_adelmo[:,97], c="blue", marker="v")
plt.scatter(trainingData_carlitos[:,6], trainingData_carlitos[:,97], c="purple", marker="*")
plt.scatter(trainingData_pedro[:,6], trainingData_pedro[:,97], c="cyan", marker="H")
plt.scatter(trainingData_jeremy[:,6], trainingData_jeremy[:,97], c="green", marker="D")
plt.show()

#Scatter plot arm workouts
plt.figure(3)
plt.title("Total Accel Arm Graph")
plt.xlabel("total_accel_arm")
plt.ylabel("total_accel_forearm")
plt.scatter(trainingData_eurico[:,44], trainingData_eurico[:,135], c="red", marker="o")
plt.scatter(trainingData_adelmo[:,44], trainingData_adelmo[:,135], c="blue", marker="v")
plt.scatter(trainingData_carlitos[:,44], trainingData_carlitos[:,135], c="purple", marker="*")
plt.scatter(trainingData_pedro[:,44], trainingData_pedro[:,135], c="cyan", marker="H")
plt.scatter(trainingData_jeremy[:,6], trainingData_jeremy[:,97], c="green", marker="D")
plt.show()


#Bar graph of each label frequency
plt.figure(4)
subjects, count = np.unique(labels, return_counts = True)
plt.bar(subjects, count)
plt.ylabel("Frequency")
plt.xlabel("Subject")
plt.title("Frequency of Subject")
plt.xticks(labels)
plt.show()

