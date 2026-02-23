#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/*
    A Storage Interface which has a write fn
*/
typedef struct Storage
{
    const struct StorageOps *ops;   // Like vtable pointer
    void *impl;                     // Concrete implementation container
} Storage;

/*
    2nd way of Implementing using struct embedding: one less poitner deref and better locality

    typedef struct Storage
    {
        const struct StorageOps *ops;   // Like vtable pointer
    } Storage;

    /// Memory Storage
    struct memory_impl
    {
        Storage base;
        char buffer[256];
    };
*/

/*
    Interface methods are defined function pointers.
    Each concrete implementation defines a function with same syntax
*/
typedef void (*write_fn)(struct Storage *s, const char* data);

/*
    Manual VTable which contains all interface methods.
    Concreate implementation set their implementations here.
*/
typedef struct StorageOps {
    write_fn write;
} StorageOps;


/// Memory Storage
struct memory_impl
{
    char buffer[256];
};

void memory_write(struct Storage* s, const char* data)
{
    struct memory_impl *m = s->impl;
    strcpy(m->buffer, data);
    printf("MemoryStorage wrote: %s\n", m->buffer);
}

struct StorageOps memory_ops = {
    .write = memory_write
};

/// File Storage
struct file_impl
{
    FILE *fp;
};

void file_write(struct Storage* s, const char* data)
{
    struct file_impl *f = s->impl;
    fprintf(f->fp, "%s\n", data);
    fflush(f->fp);
    printf("FileStorage wrote to file\n");
}

struct StorageOps file_ops = {
    .write = file_write
};

int main()
{
    // memory
    struct memory_impl mem;
    struct Storage mem_storage = {
        .ops = &memory_ops,
        .impl = &mem,
    };

    mem_storage.ops->write(&mem_storage, "hello memory");
    
    // file
    struct file_impl file = {
        .fp = fopen("out.txt", "w"),
    };
    struct Storage file_storage = {
        .ops = &file_ops,
        .impl = &file
    };

    file_storage.ops->write(&file_storage, "hello file");

    fclose(file.fp);
    return 0;
}