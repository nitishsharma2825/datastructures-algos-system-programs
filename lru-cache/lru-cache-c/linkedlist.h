#ifndef LINKEDLIST_H
#define LINKEDLIST_H

#include "pthread.h"

typedef struct Node {
    int key;
    int value;
    struct Node* next; // can dynamically change so pointer instead of value
} Node;

typedef struct LinkedList {
    int count;
    Node* head;
    Node* tail;
    pthread_mutex_t lock;
} LinkedList;

Node* initNode(int key, int value);
void freeNode(Node* node);
void initLinkedList(LinkedList* list);
void freeLinkedList(LinkedList* list);
void listAdd(LinkedList* list, int key, int value);
void listRemove(LinkedList* list, int key);
int listGet(LinkedList* list, int key);

#endif