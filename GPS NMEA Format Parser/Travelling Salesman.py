import queue
from math import sqrt

class GPS:

    def __init__(self, readings):
        self.readings = readings
        self.validate()
        self.distDict = {}
        self.getDistances()

    def start(self):
        return self.readings[0]

    def rest(self):
        return self.readings[1:]
    
    def validate(self):
        assert all([(type(i) == tuple and len(i) == 2) for i in self.readings])

    def getDistances(self):
        for tup1 in self.readings:
            for tup2 in self.readings:
                self.distDict[(tup1, tup2)] = self.euclideanDistance(tup1, tup2) # graph representation
        
        

    def euclideanDistance(coordinate1: tuple, coordinate2: tuple):
        return sqrt((coordinate1[0] - coordinate2[0]) ** 2 + (coordinate1[1] - coordinate2[1]) ** 2)
    


def TSMA(coor: GPS):
    
    return
