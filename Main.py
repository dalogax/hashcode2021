import numpy as np
import pandas as pd
import sys
import math
from itertools import combinations

class Book:
    def __init__(self, score, id, state=None):
        self.score = int(score)
        self.id = id
        self.state = state

    def __repr__(self):
        return "Book[id={}, score={}, state={}".format(self.id,self.score,self.state)


class Library:
    def __init__(self, id, books, n_signup, n_send, scanning_days, state=None, score=0):
        self.id = id
        self.books = books
        self.pending_books = self.sort_books()
        self.processed_books = []

        self.n_signup = n_signup
        self.n_send = n_send
        self.state = state
        self.score = int(score)

        self.av_days_to_process = scanning_days - self.n_signup
        self.max_score = self.get_max_score()
        self.efficiency = self.get_efficiency()

    def sort_books(self):
        return sorted(self.books, key=lambda book: book.score)

    def get_max_score(self):
        max_books_to_process = self.av_days_to_process * self.n_send
        scores_books = list(map(lambda book: book.score, self.books))
        return np.sum(scores_books[:max_books_to_process])

    def get_total_days_to_process(self):
        return math.ceil(len(self.books) / self.n_send)

    def get_efficiency(self):
        return self.get_max_score() / (self.get_total_days_to_process() + self.n_signup)

    def __repr__(self):
        return "Lib[id={}, state={}, n_signup={}, n_send={}, books={}]".format(self.id, self.state, self.n_signup, self.n_send, self.books)


def readInput(file):
    with open(file, "r", encoding="utf-8") as f:
        num_books, num_lib, scanning_days = [int(x) for x in f.readline().split(" ")]
        books = [Book(x, i) for i, x in enumerate(f.readline().split(" "))]

        libraries = []
        for x in range(num_lib):
            _, signup_days, send_days = [int(x) for x in f.readline().split(" ")]
            library_books = [books[int(x)] for x in f.readline().split(" ")]
            libraries.append(Library(id=x, books=library_books, n_signup=signup_days, n_send=send_days, scanning_days=scanning_days))
    return books, libraries, scanning_days

def writeSolution(libraries,fileName):
    with open("outputs/"+fileName, "w", encoding="utf-8") as f:
        print(len(libraries), file=f)
        for key, l in libraries.items():
            print(l.id, len(l.processed_books), file=f, sep=" ")
            print(" ".join(str(b.id) for b in l.processed_books), file=f)


def score_library(library):
    return np.sum(list(map(lambda book: book.score, library.books)))


def get_score(libraries):
    return np.sum(list(map(score_library, libraries)))

def popBestLibraryToRegister(libraries,signed_up_libraries,processed_books_ids):
    return libraries.pop()

def is_overlaped(l):
    overlap_in = []
    is_overlap = False
    for ix1, ix2 in combinations(l, r=2):
        if len(set(ix1[1]).intersection(set(ix2[1]))) > 0:
            is_overlap = True
            overlap_in.append((ix1, ix2))

    return is_overlap, overlap_in


flatten = lambda l: [item for sublist in l for item in sublist]

def sendBooks(signed_up_libraries,processed_books_ids):

    l_libraries = []
    for key, library in signed_up_libraries.items():
        l_temp = []
        while len(l_temp) <= library.n_send:
            if len(library.pending_books) > 0:
                book = library.pending_books[0]
                library.pending_books = library.pending_books[1:]
                if book.id not in processed_books_ids:
                    l_temp.append(book)
            else:
                break

        l_libraries.append((library, l_temp))

    is_overlap, overlap_in = is_overlaped(l_libraries)

    if not is_overlap:
        for library, l_temp in l_libraries:
            if len(l_temp) > 0:
                for id in list(map(lambda book: book.id, l_temp)):
                    processed_books_ids.append(id)

                for b in l_temp:
                    library.processed_books.append(b)
    else:
        pass

def getSolution(books, libraries, scanning_days):
    processed_books_ids = []
    signed_up_libraries = dict()
    signing_up = False
    signing_up_library = libraries[0]

    # Temporal, while interactions between libraries while the best library to register only depends on the pending libraries
    sorted_pending_libraries = sorted(libraries, key= lambda x: x.get_efficiency()) # ascending order
    for d in range(scanning_days):
        if not signing_up:
            signing_up_library = popBestLibraryToRegister(sorted_pending_libraries,signed_up_libraries,processed_books_ids)
            signing_up = True
        else:
            if signing_up_library.n_signup == 0:
                signed_up_libraries[signing_up_library.id] = signing_up_library
                signing_up = False
            else:
                signing_up_library.n_signup -= 1
        sendBooks(signed_up_libraries,processed_books_ids)

    return signed_up_libraries

if __name__ == "__main__":
    input_file = "c.txt"
    books, libraries, scanning_days = readInput("inputs/"+input_file)
    #print(books)
    #print(libraries)
    #print(scanning_days)

    solution_libraries = getSolution(books, libraries, scanning_days)
    writeSolution(solution_libraries,input_file[0]+".out")