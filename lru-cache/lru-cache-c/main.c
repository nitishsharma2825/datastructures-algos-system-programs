#include "linkedlist.h"
#include <stdio.h>
#include <time.h>

#define THREADS 4
#define OPS_PER_THREAD 250000

void singlethreadbenchmark(LinkedList* list)
{
    const int N = 1000000;
    clock_t start = clock();
    for (int i = 0; i < N; i++) {
        listAdd(list, i, i*10);
    }
    clock_t end = clock();

    double elapsed = (double)(end - start) / CLOCKS_PER_SEC;
    printf("Single thread: inserted %d nodes in %.3f seconds\n", N, elapsed);
}

typedef struct {
    LinkedList* list;
    int startKey;
} ThreadArgs;

void* task(void* arg)
{
    ThreadArgs* args = arg;
    for (int i = 0; i < OPS_PER_THREAD; i++) {
        int key = args->startKey + i;
        listAdd(args->list, key, key * 2);
    }
    return NULL;
}

void multithreadedbenchmark(LinkedList* list)
{
    pthread_t threads[THREADS];
    ThreadArgs args[THREADS];

    clock_t start = clock();

    for (int i = 0; i < THREADS; i++) {
        args[i].list = list;
        args[i].startKey = i * OPS_PER_THREAD;
        pthread_create(&threads[i], NULL, task, &args[i]);
    }

    for (int i = 0; i < THREADS; i++) {
        pthread_join(threads[i], NULL);
    }

    clock_t end = clock();

    double elapsed = (double)(end - start) / CLOCKS_PER_SEC;
    printf("%d threads: inserted %d total in %.3f seconds\n", THREADS, THREADS * OPS_PER_THREAD, elapsed);
}

int main(void)
{
    LinkedList list; // allocated on stack, disappears when function ends
    initLinkedList(&list);

    singlethreadbenchmark(&list);

    multithreadedbenchmark(&list);

    freeLinkedList(&list);
    return 0;
}