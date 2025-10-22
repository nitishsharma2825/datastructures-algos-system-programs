#include <stdlib.h>

#include "linkedlist.h"

Node* initNode(int key, int value)
{
    Node* newNode = (Node*)calloc(1, sizeof(Node));
    newNode->key = key;
    newNode->value = value;
    newNode->next = NULL;
    return newNode;
}

void freeNode(Node* node)
{
    free(node);
}

void initLinkedList(LinkedList* list)
{
    list->count = 0;

    list->head = initNode(-1, -1);
    if (list->head == NULL) exit(1);

    list->tail = initNode(-1, -1);
    if (list->tail == NULL) {
        free(list->head);
        exit(1);
    }

    list->head->next = list->tail;
    pthread_mutex_init(&list->lock, NULL);
}

void freeLinkedList(LinkedList* list)
{
    Node* cur = list->head;
    Node* toDelete = cur;
    while (cur != list->tail) {
        cur = cur->next;
        free(toDelete);
        toDelete = cur;
    }
    free(cur);
}

void listAdd(LinkedList* list, int key, int value)
{
    pthread_mutex_lock(&list->lock);
    Node* newNode = initNode(key, value);
    newNode->next = list->head->next;
    list->head->next = newNode;
    list->count++;
    pthread_mutex_unlock(&list->lock);
}

void listRemove(LinkedList* list, int key)
{
    pthread_mutex_lock(&list->lock);
    Node* prev = list->head;
    Node* cur = list->head->next;
    while (cur != list->tail) {
        if (cur->key == key) {
            Node* toDelete = cur;
            prev->next = cur->next;
            cur = cur->next;
            freeNode(toDelete);
            list->count--;
            return;
        } else {
            prev = cur;
            cur = cur->next;
        }
    }
    pthread_mutex_unlock(&list->lock);
}

int listGet(LinkedList* list, int key)
{
    Node* cur = list->head->next;
    while (cur != list->tail) {
        if (cur->key == key) return cur->value;
        cur = cur->next;
    }
    return -1;
}