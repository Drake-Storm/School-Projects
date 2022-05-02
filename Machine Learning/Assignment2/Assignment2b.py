"""
Assignment 2b
This program reads data from the Algerian_forest_fires_dataset_UPDATE.csv file, then splits the data into labels and raw data.
Drake Storm Hackett, 000783796, 2021"""

import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
from sklearn import tree

##Get the data
rawData = np.loadtxt("Algerian_forest_fires_dataset_UPDATE.csv", dtype=str, delimiter=",", skiprows=2)
labels = rawData[:,-1]
theData = rawData[:, :-1].astype(float)

##The decision tree algorithm
def decisionTree(traindata, trainlabels, testdata, testlabels, criterion, maxdepth, minsplit):
    """runs a version of the decision tree algorithm and returns the accuracy
    traindata/trainlabels = the training data and labels
    testdata/testlabels = the testing data and labels
    criterion = the criterion for the decision tree classifier
    maxdepth = the max_depth for the decision tree classifier
    minsplit = the min_samples_split for the decision tree classifier
    """
    if(maxdepth == "None"):
        clf = tree.DecisionTreeClassifier(criterion = criterion, min_samples_split = minsplit)
    else:
        clf = tree.DecisionTreeClassifier(criterion = criterion, max_depth = maxdepth, min_samples_split = minsplit)
    clf = clf.fit(traindata, trainlabels)
    predictions = clf.predict(testdata)
    correct = (predictions == testlabels).sum()
    return (correct/len(predictions))*100

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

    #criterion: gini, max_depth: None
    v1[run] = decisionTree(trainData, trainLabels, testData, testLabels, "gini", "None", 2)
    v2[run] = decisionTree(trainData, trainLabels, testData, testLabels, "gini", "None", 4)
    v3[run] = decisionTree(trainData, trainLabels, testData, testLabels, "gini", "None", 6)

    #criterion: gini, max_depth: 5
    v4[run] = decisionTree(trainData, trainLabels, testData, testLabels, "gini", 5, 2)
    v5[run] = decisionTree(trainData, trainLabels, testData, testLabels, "gini", 5, 4)
    v6[run] = decisionTree(trainData, trainLabels, testData, testLabels, "gini", 5, 6)

    #criterion: entropy, max_depth: None
    v7[run] = decisionTree(trainData, trainLabels, testData, testLabels, "entropy", "None", 2)
    v8[run] = decisionTree(trainData, trainLabels, testData, testLabels, "entropy", "None", 4)
    v9[run] = decisionTree(trainData, trainLabels, testData, testLabels, "entropy", "None", 6)

    #criterion: entropy, max_depth: 5
    v10[run] = decisionTree(trainData, trainLabels, testData, testLabels, "entropy", 5, 2)
    v11[run] = decisionTree(trainData, trainLabels, testData, testLabels, "entropy", 5, 4)
    v12[run] = decisionTree(trainData, trainLabels, testData, testLabels, "entropy", 5, 6)

## Print Results
print("Algerian Forest Fires Data Tests")
print("")
print("criterion: gini, max_depth: None, min_sample_split: 2")
print("Average Accuracy: ", np.round(np.average(v1), decimals = 2))
print(v1)

print("")
print("criterion: gini, max_depth: None, min_sample_split: 4")
print("Average Accuracy: ", np.round(np.average(v2), decimals = 2))
print(v2)

print("")
print("criterion: gini, max_depth: None, min_sample_split: 6")
print("Average Accuracy: ", np.round(np.average(v3), decimals = 2))
print(v3)

print("")
print("criterion: gini, max_depth: 5, min_sample_split: 2")
print("Average Accuracy: ", np.round(np.average(v4), decimals = 2))
print(v4)

print("")
print("criterion: gini, max_depth: 5, min_sample_split: 4")
print("Average Accuracy: ", np.round(np.average(v5), decimals = 2))
print(v5)

print("")
print("criterion: gini, max_depth: 5, min_sample_split: 6")
print("Average Accuracy: ", np.round(np.average(v6), decimals = 2))
print(v6)

print("")
print("criterion: entropy, max_depth: None, min_sample_split: 2")
print("Average Accuracy: ", np.round(np.average(v7), decimals = 2))
print(v7)

print("")
print("criterion: entropy, max_depth: None, min_sample_split: 4")
print("Average Accuracy: ", np.round(np.average(v8), decimals = 2))
print(v8)

print("")
print("criterion: entropy, max_depth: None, min_sample_split: 6")
print("Average Accuracy: ", np.round(np.average(v9), decimals = 2))
print(v9)

print("")
print("criterion: entropy, max_depth: 5, min_sample_split: 2")
print("Average Accuracy: ", np.round(np.average(v10), decimals = 2))
print(v10)

print("")
print("criterion: entropy, max_depth: 5, min_sample_split: 4")
print("Average Accuracy: ", np.round(np.average(v11), decimals = 2))
print(v11)

print("")
print("criterion: entropy, max_depth: 5, min_sample_split: 6")
print("Average Accuracy: ", np.round(np.average(v12), decimals = 2))
print(v12)


##Provide graph of averages
plt.figure(1)
plt.title("Decision Trees Summary")
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