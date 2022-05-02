"""
Assignment 2a
This program reads data from the Algerian_forest_fires_dataset_UPDATE.csv file, then splits the data into labels and raw data. Using a classify function and a function to run each version separately it loops 50 times through, each time splitting the data and labels into testing and training and it runs each version of the kNN algorithm created in the classify function. Testing 3 different values for k, 2 different distance computations, and non-normalized vs min-max normalized data.
Drake Storm Hackett, 000783796, 2021"""

import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
from matplotlib import pyplot as plt

##Read data

rawData = np.loadtxt("Algerian_forest_fires_dataset_UPDATE.csv", dtype=str, delimiter=",", skiprows=2)
labels = rawData[:,-1]
theData = rawData[:, :-1].astype(float)


##The kNN algorithm

def Classify(newItem, data, labels, k, distCompute):
    """Computes the distance between the new item and each item in the data, then gets k nearest items to classify the new item.
    newItem = the item we wish to classify
    data = the data from the file
    labels = labels for each item in data
    k = how many nearest items we use to classify
    distCompute = which distance calculation we will use
    """
    distance = 0
    if(distCompute == "euclidian"):
        distance = np.sqrt(((data - newItem)**2).sum(axis = 1))

    if(distCompute == "manhattan"):
        distance = np.abs((data - newItem).sum(axis = 1))

    #sort the distances and their respective labels
    votingLabels = labels[distance.argsort()]

    #get the voters and tally results
    voters = votingLabels[:k]
    theVoters, counts = np.unique(voters, return_counts=True)
    index=np.argmax(counts)
    return theVoters[index]

## Run version

def runVersion(data, k, distCompute):
    """runs a version of the kNN algorithm and returns the accuracy
    data = the testData either normalized or not
    k = how many nearest items we use to classify
    distCompute = which distance calculation we will use
    """
    predictions = np.apply_along_axis(Classify, 1, data, trainData, trainLabels, k, distCompute)
    accuracyScore = accuracy_score(testLabels, predictions)*100
    return np.round(accuracyScore, decimals = 2)

## Split the data and run each version

#12 arrays for 12 versions to keep track of accuracies
v1 = np.zeros(50)
v2 = np.zeros(50)
v3 = np.zeros(50)
v4 = np.zeros(50)
v5 = np.zeros(50)
v6 = np.zeros(50)
v7 = np.zeros(50)
v8 = np.zeros(50)
v9 = np.zeros(50)
v10 = np.zeros(50)
v11 = np.zeros(50)
v12 = np.zeros(50)


for run in range(0, 50):
    trainData, testData, trainLabels, testLabels = train_test_split(theData, labels, train_size=0.75)
    dataNormalized = (testData - testData.min(axis=0))/(testData.max(axis=0) - testData.min(axis=0))
    #non-normalized euclidian
    v1[run] = runVersion(testData, 3, "euclidian")
    v2[run] = runVersion(testData, 7, "euclidian")
    v3[run] = runVersion(testData, 15, "euclidian")

    #non-normalized manhattan
    v4[run] = runVersion(testData, 3, "manhattan")
    v5[run] = runVersion(testData, 7, "manhattan")
    v6[run] = runVersion(testData, 15, "manhattan")


    #normalized euclidian
    v7[run] = runVersion(dataNormalized, 3, "euclidian")
    v8[run] = runVersion(dataNormalized, 7, "euclidian")
    v9[run] = runVersion(dataNormalized, 15, "euclidian")

    #normalized manhattan
    v10[run] = runVersion(dataNormalized, 3, "manhattan")
    v11[run] = runVersion(dataNormalized, 7, "manhattan")
    v12[run] = runVersion(dataNormalized, 15, "manhattan")

## Print results
print("Algerian Forest Fires Data Tests")
print("")
print("k: 3, Distance: Euclidian, Normalized: No")
print("Average Accuracy: ", np.round(np.average(v1), decimals = 2))
print(v1)

print("")
print("k: 7, Distance: Euclidian, Normalized: No")
print("Average Accuracy: ", np.round(np.average(v2), decimals = 2))
print(v2)

print("")
print("k: 15, Distance: Euclidian, Normalized: No")
print("Average Accuracy: ", np.round(np.average(v3), decimals = 2))
print(v3)

print("")
print("k: 3, Distance: Manhattan, Normalized: No")
print("Average Accuracy: ", np.round(np.average(v4), decimals = 2))
print(v4)


print("")
print("k: 7, Distance: Manhattan, Normalized: No")
print("Average Accuracy: ", np.round(np.average(v5), decimals = 2))
print(v5)

print("")
print("k: 15, Distance: Manhattan, Normalized: No")
print("Average Accuracy: ", np.round(np.average(v6), decimals = 2))
print(v6)

print("")
print("k: 3, Distance: Euclidian, Normalized: Yes")
print("Average Accuracy: ", np.round(np.average(v7), decimals = 2))
print(v7)

print("")
print("k: 7, Distance: Euclidian, Normalized: Yes")
print("Average Accuracy: ", np.round(np.average(v8), decimals = 2))
print(v8)

print("")
print("k: 15, Distance: Euclidian, Normalized: Yes")
print("Average Accuracy: ", np.round(np.average(v9), decimals = 2))
print(v9)

print("")
print("k: 3, Distance: Manhattan, Normalized: Yes")
print("Average Accuracy: ", np.round(np.average(v10), decimals = 2))
print(v10)

print("")
print("k: 7, Distance: Manhattan, Normalized: Yes")
print("Average Accuracy: ", np.round(np.average(v11), decimals = 2))
print(v11)

print("")
print("k: 15, Distance: Manhattan, Normalized: Yes")
print("Average Accuracy: ", np.round(np.average(v12), decimals = 2))
print(v12)

##Provide graph of averages
plt.figure(1)
plt.title("kNN Summary")
plt.xlabel("Run")
plt.ylabel("Accuracy")
plt.plot(v1, c="red", linestyle="", marker=".")
plt.plot(v2, c="blue", linestyle="", marker=".")
plt.plot(v3, c="green", linestyle="", marker=".")
plt.plot(v4, c="yellow", linestyle="", marker=".")
plt.plot(v5, c="cyan", linestyle="", marker=".")
plt.plot(v6, c="purple", linestyle="", marker=".")
plt.plot(v7, c="magenta", linestyle="", marker=".")
plt.plot(v8, c="lime", linestyle="", marker=".")
plt.plot(v9, c="orange", linestyle="", marker=".")
plt.plot(v10, c="brown", linestyle="", marker=".")
plt.plot(v11, c="pink", linestyle="", marker=".")
plt.plot(v12, c="maroon", linestyle="", marker=".")
plt.show()

