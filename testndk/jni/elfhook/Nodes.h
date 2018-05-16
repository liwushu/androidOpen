//
// Created by shuliwu on 2018/5/6.
//

#ifndef FACEU2_NODES_H
#define FACEU2_NODES_H

#endif //FACEU2_NODES_H

struct MallocNode{
    int addr;
    int size;
};

struct MallocNodeList{
    MallocNode node;
    MallocNodeList *next;
};