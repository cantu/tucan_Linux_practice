
/************************************************************************/
//《零基础学数据结构》第九章，有关二叉树的操作
//创建时间：2011/7/21
//作者：涂灿
/************************************************************************/

//=======================二叉树操作=============

#include"BitTree.h"
#include <stdlib.h> //malloc函数在这个库中定义的
#include <stdio.h>		//printf函数在这个库中定义的

/************************************************************************/
// 初始化一个二叉树，P259
/************************************************************************/
void InitBitTree( BitTree *T )
{
	T = NULL;
}

/************************************************************************/
// 销毁二叉树，如果二叉树存在则将其存储空间释放 P260
/************************************************************************/
void DestroyBitTree ( BitTree *T )
{
	if( *T )
	{
		if( (*T)->lchild )
			DestroyBitTree( &( (*T)->lchild ) );
		if( (*T)->rchild )
			DestroyBitTree( &( (*T)->rchild ) );
		free( *T );
		*T = NULL;
	}
}

/************************************************************************/
// 创建二叉树，先生成二叉树的根节点，再递归创建左子树和右子数	P260
/************************************************************************/
void CreatBitTree ( BitTree *T )
{
	DataType ch;
	scanf( "%c",&ch );
	if( ch=='#' )
		*T = NULL;
	else
	{
		*T = (BitTree) malloc( sizeof(BitNode) );
		if( !(*T) )	return OVERFLOW;
		(*T)->data = ch;
		CreatBitTree( &( (*T)->lchild ) );
		CreatBitTree( &( (*T)->rchild ) );
	}
}

/************************************************************************/
// 二叉树的左插入操作，将子树C插到T中	P260
/************************************************************************/
int InsertLeftChild( BitTree p, Bittree c )
{
	if( p )
	{	//p为原来的树，c为新增的子树
		c->lchild = p->lchild;
		p->lchild = c;
		return OK;
	}
	return 0;
}

/************************************************************************/
// 二叉树的右插入操作，将子树C插到T中	P261
/************************************************************************/
int InsertRightChild( BitTree p, Bittree c )
{
	if( p )
	{	//p为原来的树，c为新增的子树
		c->rchild = p->rchild;
		p->rchild = c;
		return OK;
	}
	return 0;
}
/************************************************************************/
//  查找二叉树结点的指针， 将元素为e的结点指针返回	P261
/************************************************************************/
BitTree Point( BitTree T, DataType e )
{

