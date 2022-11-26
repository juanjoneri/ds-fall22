import pandas as pd
import networkx as nx

from functools import cached_property
from collections import deque

class Dataset():

    def __init__(self, dataset):
        self._dataset = dataset

    @cached_property
    def coordinates(self):
        df = pd.read_csv(f'{self._dataset}/coordinates.csv', header=None)
        data = list(df.itertuples(index=False, name=None))
        return data
    
    @cached_property
    def edges(self):
        df = pd.read_csv(f'{self._dataset}/edges.csv', header=None)
        data = list(df.itertuples(index=False, name=None))
        return data
    
    @cached_property
    def G(self):
        G = nx.Graph()
        G.add_nodes_from(range(len(self.coordinates)))
        for x, y, weight in self.edges:
            G.add_edge(x, y, weight=weight)
        return G


if __name__ == '__main__':
    dataset = Dataset('rand-100')
    d = dataset.coordinates
    s = dataset.edges
    print(dataset.G)