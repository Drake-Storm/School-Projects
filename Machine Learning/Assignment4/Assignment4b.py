"""
This program reads in 5 different data files, splits each into training/testing data and labels, then trains both a Muli-Layer Perceptron and Decision Tree algorithm, tests them, and outputs the results along with the configuration of the MLP classifier that got the best accuracy with the smallest network and lowest iterations.
Drake Storm Hackett, 000783796, 2021"""

import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score
from sklearn.neural_network import MLPClassifier
from sklearn import tree



## Datafile 1
clf = tree.DecisionTreeClassifier()
mlp = MLPClassifier(learning_rate_init = 0.3, hidden_layer_sizes = ())
filename = "files/000783796_1.csv"
rawData = np.loadtxt(filename, dtype=int, delimiter=",")
labels = rawData[:,-1]
theData = rawData[:, :-1].astype(float)
trainData, testData, trainLabels, testLabels = train_test_split(theData, labels, train_size=0.75)

#Train and predict
clf.fit(trainData, trainLabels)
mlp.fit(trainData, trainLabels)
iterations = mlp.n_iter_
dtPredictions = clf.predict(testData)
mlpPredictions = mlp.predict(testData)

#Print Results
print("File: " + filename)
print("Decision Tree: " + "{:.2f}".format(accuracy_score(testLabels, dtPredictions) * 100) + "%")
print("MLP: hidden layers = [], LR = 0.3")
print("{:.2f}".format(accuracy_score(testLabels, mlpPredictions) * 100) + "% Accuracy, " + str(iterations) + " Iterations\n")


## Datafile 2
clf = tree.DecisionTreeClassifier()
mlp = MLPClassifier(learning_rate_init = 0.03, hidden_layer_sizes = ())
filename = "files/000783796_2.csv"
rawData = np.loadtxt(filename, dtype=int, delimiter=",")
labels = rawData[:,-1]
theData = rawData[:, :-1].astype(float)
trainData, testData, trainLabels, testLabels = train_test_split(theData, labels, train_size=0.75)

#Train and predict
clf.fit(trainData, trainLabels)
mlp.fit(trainData, trainLabels)
iterations = mlp.n_iter_
dtPredictions = clf.predict(testData)
mlpPredictions = mlp.predict(testData)

#Print Results
print("File: " + filename)
print("Decision Tree: " + "{:.2f}".format(accuracy_score(testLabels, dtPredictions) * 100) + "%")
print("MLP: hidden layers = [], LR = 0.03")
print("{:.2f}".format(accuracy_score(testLabels, mlpPredictions) * 100) + "% Accuracy, " + str(iterations) + " Iterations\n")


## Datafile 3
clf = tree.DecisionTreeClassifier()
hiddenLayers = (10, 7, 5, 3)
LR = 0.0001
mlp = MLPClassifier(activation = "identity", learning_rate_init = LR, hidden_layer_sizes = hiddenLayers, max_iter = 100000, tol = 0.00000001, n_iter_no_change = 5)
filename = "files/000783796_3.csv"
rawData = np.loadtxt(filename, dtype=int, delimiter=",")
labels = rawData[:,-1]
theData = rawData[:, :-1].astype(float)
trainData, testData, trainLabels, testLabels = train_test_split(theData, labels, train_size=0.75)

#Train and predict
clf.fit(trainData, trainLabels)
mlp.fit(trainData, trainLabels)
iterations = mlp.n_iter_
dtPredictions = clf.predict(testData)
mlpPredictions = mlp.predict(testData)

#Print Results
print("File: " + filename)
print("Decision Tree: " + "{:.2f}".format(accuracy_score(testLabels, dtPredictions) * 100) + "%")
print("MLP: hidden layers = " + str(hiddenLayers) + ", LR = " + str(LR) + ", Activation = identity, max_iter = 100000, tol = 0.00000001, n_iter_no_change = 5")
print("{:.2f}".format(accuracy_score(testLabels, mlpPredictions) * 100) + "% Accuracy, " + str(iterations) + " Iterations\n")


## Datafile 4
clf = tree.DecisionTreeClassifier()
hiddenLayers = ()
LR = 0.3
mlp = MLPClassifier(learning_rate_init = LR, hidden_layer_sizes = hiddenLayers)
filename = "files/000783796_4.csv"
rawData = np.loadtxt(filename, dtype=int, delimiter=",")
labels = rawData[:,-1]
theData = rawData[:, :-1].astype(float)
trainData, testData, trainLabels, testLabels = train_test_split(theData, labels, train_size=0.75)

#Train and predict
clf.fit(trainData, trainLabels)
mlp.fit(trainData, trainLabels)
iterations = mlp.n_iter_
dtPredictions = clf.predict(testData)
mlpPredictions = mlp.predict(testData)

#Print Results
print("File: " + filename)
print("Decision Tree: " + "{:.2f}".format(accuracy_score(testLabels, dtPredictions) * 100) + "%")
print("MLP: hidden layers = " + str(hiddenLayers) + ", LR = " + str(LR))
print("{:.2f}".format(accuracy_score(testLabels, mlpPredictions) * 100) + "% Accuracy, " + str(iterations) + " Iterations\n")


## Datafile 5
clf = tree.DecisionTreeClassifier()
hiddenLayers = ()
LR = 0.1
mlp = MLPClassifier(learning_rate_init = LR, hidden_layer_sizes = hiddenLayers)
filename = "dataset/staDynVxHeaven2698Lab.csv"
rawData = np.loadtxt(filename, dtype=float, delimiter=",", skiprows = 1)
labels = rawData[:,-1]
theData = rawData[:, :-1].astype(float)
trainData, testData, trainLabels, testLabels = train_test_split(theData, labels, train_size=0.75)

#Train and predict
clf.fit(trainData, trainLabels)
mlp.fit(trainData, trainLabels)
iterations = mlp.n_iter_
dtPredictions = clf.predict(testData)
mlpPredictions = mlp.predict(testData)

#Print Results
print("File: " + filename)
print("Decision Tree: " + "{:.2f}".format(accuracy_score(testLabels, dtPredictions) * 100) + "%")
print("MLP: hidden layers = " + str(hiddenLayers) + ", LR = " + str(LR))
print("{:.2f}".format(accuracy_score(testLabels, mlpPredictions) * 100) + "% Accuracy, " + str(iterations) + " Iterations\n")
