"""
This program will use kMeans clustering with 12 different values for k to get colour values for each pixel of an image, then quantize the image using those colour clusters then using the k value that made the best clusters for quantizing it will quantize a separate image using the clusters from the first image.
There is no distinct 'elbow' for this image, maybe a bit of one at k=3 and k=4. Since there was no obvious elbow I chose to use k=7 because I liked that one best.
Drake Storm Hackett, , 000783796, 2021
"""
from sklearn.cluster import KMeans
import numpy as np
from skimage import io
import matplotlib.pyplot as plt

##Get initial image and flatten it
AlbumCover = io.imread("Photos/Danny Attitude City.jpg")
plt.figure("Original")
plt.title("NSP Attitude City.jpg")
plt.imshow(AlbumCover)
plt.axis('off')

imageShape = AlbumCover.shape

FlatAlbumCover = AlbumCover.reshape((imageShape[0] * imageShape[1], imageShape[2]))

##Set up variables for loop for Task 1
TheAlbumCovers = [np.array(FlatAlbumCover), np.array(FlatAlbumCover), np.array(FlatAlbumCover), np.array(FlatAlbumCover), np.array(FlatAlbumCover), np.array(FlatAlbumCover), np.array(FlatAlbumCover), np.array(FlatAlbumCover), np.array(FlatAlbumCover), np.array(FlatAlbumCover), np.array(FlatAlbumCover), np.array(FlatAlbumCover)] #This array is to store the image without it changing with each run

TheAlbumCovers = np.array(TheAlbumCovers)

inertias = np.zeros(12)
k_values = [2,3,4,5,6,7,8,9,10,11,12,13]

##Task 1
for k in range(2, 14):
    km = KMeans(n_clusters=k)
    km.fit(TheAlbumCovers[k-2])
    clusters = km.cluster_centers_
    inertias[k-2] = km.inertia_
    print("k = " + str(k) + " SSE/Inertia = " + str(km.inertia_))
    predictions = km.predict(TheAlbumCovers[k-2])

    for row in range(TheAlbumCovers[k-2].shape[0]):
        TheAlbumCovers[k-2, row] = clusters[predictions[row]]
    NewAlbumCover = TheAlbumCovers[k-2].reshape(imageShape)

    plt.figure("New Cover k=" + str(k))
    plt.title("k = " + str(k) + " inertia = " + str(inertias[k-2]))
    plt.imshow(NewAlbumCover)
    plt.axis('off')

##Task 1 k vs inertia graph
plt.figure("k vs inertia")
plt.plot(k_values, inertias)
plt.title("Danny Attitude City.jpg k vs inertia")
plt.xlabel("k")
plt.ylabel("inertia")

##Task 2
PsychedelicCat = io.imread("Photos/Psychedelic Cat.jpg")
plt.figure("Original Psychedelic Cat")
plt.title("Original Psychedelic Cat.jpg")
plt.imshow(PsychedelicCat)
plt.axis('off')

catImageShape = PsychedelicCat.shape
FlatCat = PsychedelicCat.reshape((catImageShape[0] * catImageShape[1], catImageShape[2]))
km = KMeans(n_clusters = 7)
km.fit(FlatAlbumCover)
clusters = km.cluster_centers_
predictions = km.predict(FlatCat)
for row in range(FlatCat.shape[0]):
    FlatCat[row] = clusters[predictions[row]]
newPsychedelicCat = FlatCat.reshape(catImageShape)
plt.figure("Quantized Psychedelic Cat")
plt.title("Psychedelic Cat.jpg quantized using Danny Attitude City.jpg")
plt.imshow(PsychedelicCat)
plt.axis('off')


plt.show()

