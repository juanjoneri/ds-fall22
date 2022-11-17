from scipy.sparse import csr_matrix
from scipy.sparse.csgraph import minimum_spanning_tree

X = csr_matrix([[0, 1.1, 1.7, 0, 3.8, 0],
                [0, 0, 0, 3.1, 0, 0],
                [0, 0, 0, 0, 3.8, 0],
                [0, 0, 0, 0, 0, 3.7],
                [0, 0, 0, 0, 0, 2.1],
                [0, 0, 0, 0, 0, 0]])

Tcsr = minimum_spanning_tree(X)

Tcsr.toarray().astype(int)

print(X)

print(Tcsr.toarray().astype(int))
