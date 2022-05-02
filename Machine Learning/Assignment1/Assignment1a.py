"""
Assignment 1a
Gets some names for each player from the user, puts those names into a shuffled numpy array, then ask each player to press enter a specified (by the user) number of times, records the time in between each press in a 2D numpy array. Output the mean time for each player, the fastest and slowest mean times, and the fastest and slowest interval times.
Drake Storm Hackett, 000783796
"""
import numpy as np
from time import time

##Get game details and player names
numberOfPlayers = int(input("How many players? "))
numIntervals = int(input("How many time intervals? "))
players = set()
print("Enter", numberOfPlayers, "player names:")
iteration = 0
while(iteration < numberOfPlayers):
    name = input().strip().capitalize()
    if name in players:
        print("Name already entered")
    elif not name:
        print("Name cannot be empty")
    else:
        players.add(name)
        iteration +=1
thePlayers = np.array(tuple(players))
np.random.shuffle(thePlayers)

##Get players to play the game
scores = []
for player in thePlayers:
    theScore = []
    print(player, "Press enter", numIntervals + 1, "times.")
    input()
    time1 = time()
    for interval in np.arange(numIntervals):
        input()
        time2 = time()
        theScore.append(round(time2-time1, 3))
        time1 = time()
    scores.append(theScore)
allScores = np.array(scores)
indices = thePlayers.argsort()
thePlayers = thePlayers[indices]
allScores = allScores[indices]

##Output Results
print("Names:", thePlayers)
meanTimes = np.array(allScores.mean(axis=1))
print("Mean times:", meanTimes)
print("Fastest average time:", round(meanTimes.min(), 3), "by", thePlayers[meanTimes.argmin()])
print("Slowest average time:", round(meanTimes.max(), 3), "by", thePlayers[meanTimes.argmax()])
print("Fastest single time:", round(allScores.min(), 3), "by", thePlayers[np.where(allScores == allScores.min())[0]])
print("Slowest single time:", round(allScores.max(), 3), "by", thePlayers[np.where(allScores == allScores.max())[0]])
print(thePlayers)
print(allScores)


