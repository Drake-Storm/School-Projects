"""
This program runs 5 different text classification tasks using Naive Bayes, each task has 8 different versions of either the text representation or the Naive Bayes algorithm. For each version it returns a confusion matrix which gets combined with the ones from the other tasks, then it computes the micro-averaged accuracy, precision, and recall for each version.

Drake Storm Hackett, 000783796
"""

import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.metrics import confusion_matrix
from sklearn.naive_bayes import MultinomialNB
from sklearn.naive_bayes import ComplementNB
from nltk.stem import PorterStemmer as ps
import parse_reuters as parser

stopwords = ["a","able","about","across","after","all","almost","also","am","among","an","and","any","are","as","at","be","because","been","but","by","can","cannot","could","dear","did","do","does","either","else","ever","every","for","from","get","got","had","has","have","he","her","hers","him","his","how","however","i","if","in","into","is","it","its","just","least","let","like","likely","may","me","might","most","must","my","neither","no","nor","not","of","off","often","on","only","or","other","our","own","rather","said","say","says","she","should","since","so","some","than","that","the","their","them","then","there","these","they","this","tis","to","too","twas","us","wants","was","we","were","what","when","where","which","while","who","whom","why","will","with","would","yet","you","your"]

##Methods from Example Code, Week 7
def textParse(text):
    """ A utility to split a string into a list of lowercase words, removing punctuation. Gotten from Example Code, Week 7"""
    import string
    return [word.lower().strip(string.punctuation) for word in text.split()]


def createVocabList(dataSet):
    """ dataSet is a list of word lists. Returns the set of words in the dataSet
    as a list. Gotten from Example Code, Week 7"""
    vocabSet = set()  #create empty set
    for document in dataSet:
        vocabSet = vocabSet.union(set(document)) #union of the two sets
    return list(vocabSet)


def bagOfWords(vocabList, inputList):
    """ vocabList is a set of words (as a list). inputList is a list of words
    occurring in a document. Returns a list of integers indicating how many
    times each word in the vocabList occurs in the inputList. Gotten from Example Code, Week 7"""
    d = {}
    for word in inputList:
        d[word] = d.get(word,0) + 1
    bagofwords = []
    for word in vocabList:
        bagofwords.append(d.get(word,0))
    return bagofwords

##Methods to run each version/task
def runVersion(train, test, trainlabels, testlabels, stemming = False, removeStop = False, multinomial = False):
    """This function runs a version of the classifier and returns a confusion matrix
    train, test, trainlabels, testlabels = the training/testing data/labels
    stemming = boolean value respresenting whether to stem the words or not
    removeStop = boolean value representing whether to remove stop words or not
    multinomial = boolean value representing whether to use multinomialNB or complementNB
    """

    print("\nRunning Version: stemming = " + str(stemming) + ", word removal = " + str(removeStop) + ", MultinomialNB = " + str(multinomial))
    vocabWords = []
    for somewords in train:
        if stemming:
            stemmer = ps()
            for theWord in somewords:
                stemmer.stem(theWord)
                vocabWords.append(theWord)
        else:
            vocabWords.append(somewords)

    for somewords in test:
        if stemming:
            stemmer = ps()
            for theWord in somewords:
                stemmer.stem(theWord)
                vocabWords.append(theWord)
        else:
            vocabWords.append(somewords)


    print("Creating Vocab List")
    vocab = createVocabList(vocabWords)
    if removeStop:
        for word in vocab:
            if word in stopwords:
                vocab.remove(word)


    if multinomial:
        nb = MultinomialNB()
    else:
        nb = ComplementNB()

    print("Creating Vectors")
    trainvectors = []
    for someWords in train:
        trainvectors.append(bagOfWords(vocab, someWords))
    testvectors = []
    for someWords in test:
        testvectors.append(bagOfWords(vocab, someWords))

    nb.fit(trainvectors, trainlabels)
    predictions = nb.predict(testvectors)
    return confusion_matrix(testlabels, predictions)



def classify(label, type):
    """This function will run a classifcation task with 8 different versions, returning 8 confusion matrices
    label = the label we are using to classify
    type = the type of label to classify (i.e. topics, places, etc)
    """
    print("\nRunning Task: " + label + " of " + type)
    words = []
    labels = []

    print("Getting the words from corpus")
    for doc in corpus:

        words.append(np.array(textParse(doc['text'])))

        if label in doc[type]:
            labels.append(1)
        else:
            labels.append(0)
    print("Found " + str(labels.count(1)) + " " + label + " in " + str(len(labels)) + " documents.")

    words = np.array(words, dtype = object)
    labels = np.array(labels)
    trainData, testData, trainLabels, testLabels = train_test_split(words, labels, train_size=0.75)

    cm1 = runVersion(trainData, testData, trainLabels, testLabels)
    cm2 = runVersion(trainData, testData, trainLabels, testLabels, stemming = True)
    cm3 = runVersion(trainData, testData, trainLabels, testLabels, removeStop = True)
    cm4 = runVersion(trainData, testData, trainLabels, testLabels, multinomial = True)
    cm5 = runVersion(trainData, testData, trainLabels, testLabels, stemming = True, removeStop = True)
    cm6 = runVersion(trainData, testData, trainLabels, testLabels, stemming = True, multinomial = True)
    cm7 = runVersion(trainData, testData, trainLabels, testLabels, removeStop = True, multinomial = True)
    cm8 = runVersion(trainData, testData, trainLabels, testLabels, stemming = True, removeStop = True, multinomial = True)
    return cm1, cm2, cm3, cm4, cm5, cm6, cm7, cm8

##Read the corpus and run classification tasks
corpus = parser.read_reuters()
cm1, cm2, cm3, cm4, cm5, cm6, cm7, cm8 = classify('earn', 'topics')
cm1v2, cm2v2, cm3v2, cm4v2, cm5v2, cm6v2, cm7v2, cm8v2 = classify('coffee', 'topics')
cm1v3, cm2v3, cm3v3, cm4v3, cm5v3, cm6v3, cm7v3, cm8v3 = classify('crude', 'topics')
cm1v4, cm2v4, cm3v4, cm4v4, cm5v4, cm6v4, cm7v4, cm8v4 = classify('usa', 'places')
cm1v5, cm2v5, cm3v5, cm4v5, cm5v5, cm6v5, cm7v5, cm8v5 = classify('egypt', 'places')


##Report confusion matrix, accuracy, precision, and recall for each version
combined = cm1 + cm1v2 + cm1v3 + cm1v4 + cm1v5
combined2 = cm2 + cm2v2 + cm2v3 + cm2v4 + cm2v5
combined3 = cm3 + cm3v2 + cm3v3 + cm3v4 + cm3v5
combined4 = cm4 + cm4v2 + cm4v3 + cm4v4 + cm4v5
combined5 = cm5 + cm5v2 + cm5v3 + cm5v4 + cm5v5
combined6 = cm6 + cm6v2 + cm6v3 + cm6v4 + cm6v5
combined7 = cm7 + cm7v2 + cm7v3 + cm7v4 + cm7v5
combined8 = cm8 + cm8v2 + cm8v3 + cm8v4 + cm8v5

print("\nMetrics for version 1:")
accuracy = (combined[0, 0] + combined[1, 1]) / (combined[0, 0] + combined[0, 1] + combined[1, 0] + combined[1, 1])
precision = combined[0, 0] / (combined[0, 0] + combined[1, 0])
recall = combined[0, 0] / (combined[0, 0] + combined[0, 1])
print(combined)
print("Accuracy: " + str(accuracy) + "\nPrecision: " + str(precision) + "\nRecall: " + str(recall))

print("\nMetrics for version 2:")
accuracy2 = (combined2[0, 0] + combined2[1, 1]) / (combined2[0, 0] + combined2[0, 1] + combined2[1, 0] + combined2[1, 1])
precision2 = combined2[0, 0] / (combined2[0, 0] + combined2[1, 0])
recall2 = combined2[0, 0] / (combined2[0, 0] + combined2[0, 1])
print(combined2)
print("Accuracy: " + str(accuracy2 ) + "\nPrecision: " + str(precision2 )+ "\nRecall: " + str(recall2))

print("\nMetrics for version 3:")
accuracy3 = (combined3[0, 0] + combined3[1, 1]) / (combined3[0, 0] + combined3[0, 1] + combined3[1, 0] + combined3[1, 1])
precision3 = combined3[0, 0] / (combined3[0, 0] + combined3[1, 0])
recall3 = combined3[0, 0] / (combined3[0, 0] + combined3[0, 1])
print(combined3)
print("Accuracy: " + str(accuracy3 )+ "\nPrecision: " +str( precision3 )+ "\nRecall: " + str(recall3))

print("\nMetrics for version 4:")
accuracy4 = (combined4[0, 0] + combined4[1, 1]) / (combined4[0, 0] + combined4[0, 1] + combined4[1, 0] + combined4[1, 1])
precision4 = combined4[0, 0] / (combined4[0, 0] + combined4[1, 0])
recall4 = combined4[0, 0] / (combined4[0, 0] + combined4[0, 1])
print(combined4)
print("Accuracy: " + str(accuracy4) + "\nPrecision: " + str(precision4) + "\nRecall: " + str(recall4))

print("\nMetrics for version 5:")
accuracy5 = (combined5[0, 0] + combined5[1, 1]) / (combined5[0, 0] + combined5[0, 1] + combined5[1, 0] + combined5[1, 1])
precision5 = combined5[0, 0] / (combined5[0, 0] + combined5[1, 0])
recall5 = combined5[0, 0] / (combined5[0, 0] + combined5[0, 1])
print(combined5)
print("Accuracy: " + str(accuracy5) + "\nPrecision: " + str(precision5) + "\nRecall: " + str(recall5))

print("\nMetrics for version 6:")
accuracy6 = (combined6[0, 0] + combined6[1, 1]) / (combined6[0, 0] + combined6[0, 1] + combined6[1, 0] + combined6[1, 1])
precision6 = combined6[0, 0] / (combined6[0, 0] + combined6[1, 0])
recall6 = combined6[0, 0] / (combined6[0, 0] + combined6[0, 1])
print(combined6)
print("Accuracy: " + str(accuracy6) + "\nPrecision: " + str(precision6) + "\nRecall: " + str(recall6))

print("\nMetrics for version 7:")
accuracy7 = (combined7[0, 0] + combined7[1, 1]) / (combined7[0, 0] + combined7[0, 1] + combined7[1, 0] + combined7[1, 1])
precision7 = combined7[0, 0] / (combined7[0, 0] + combined7[1, 0])
recall7 = combined7[0, 0] / (combined7[0, 0] + combined7[0, 1])
print(combined7)
print("Accuracy: " + str(accuracy7) + "\nPrecision: " + str(precision7 )+ "\nRecall: " + str(recall7))

print("\nMetrics for version 8:")
accuracy8 = (combined8[0, 0] + combined8[1, 1]) / (combined8[0, 0] + combined8[0, 1] + combined8[1, 0] + combined8[1, 1])
precision8 = combined8[0, 0] / (combined8[0, 0] + combined8[1, 0])
recall8 = combined8[0, 0] / (combined8[0, 0] + combined8[0, 1])
print(combined8)
print("Accuracy: " + str(accuracy8) + "\nPrecision: " + str(precision8) + "\nRecall: " + str(recall8))


