/*
*
**/

main()
{
	printf("my pid is%d\n", getpid() );
	fork();
	fork();
	fork();
	printf("my pid id %d\n", getpid() );
}