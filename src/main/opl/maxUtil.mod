// Copyright (C) Maxime MORGE Antoine NONGAILLARD 2017

/**
* OPL 12.8.0.0 Model for reaching a MaxUtil matching of a IA problem instance 
**/
 
int M = ...; // Number of individuals
int N = ...; // Number of activities
range I = 1..M; // The set of individuals
range A = 1..N; // The set of activities
float V[I][A] = ...; // The individuals valuate the interest of the activities 
float W[I][I] = ...; // The individuals valuate the affinity of the peers
int C[A] = ...; // The capacityes of the activities 

dvar int X[I][A] in 0..1; // The decision variables 

/* Preprocessing */
float startingTime;
execute{
	var before = new Date();
	startingTime = before.getTime();
}

/* Solving the model */
maximize 
	// the utilitarian welfare
	1.0/M * sum(i in I) sum(a in A)  0.5 * (X[i][a]*V[i][a] + 1.0/(M-1) * sum(j in I) X[i][a]*X[j][a]*W[i][j]);
	// the egalitarian welfare
	//min(i in I) sum(a in A)  0.5 * (X[i][a]*V[i][a] + 1.0/(M-1) * sum(j in I) X[i][a]*X[j][a]*W[i][j]);
subject to {
	forall(a in A)
	  ct_capacity:
	  	sum(i in I) X[i][a] <= C[a];
	forall(i in I)
	  ct_singleAssignement:
	  	sum(a in A) X[i][a] <=1;
}

/* Postprocessing */
execute{
	var endTime = new Date();
	var processingTime=endTime.getTime()-startingTime //ms
	var outputFile = new IloOplOutputFile("../../../experiments/OPL/miqplOutput.txt"); //See application.conf
	outputFile.writeln(cplex.getObjValue());//U(M)
	outputFile.writeln(processingTime);//T in millisecond
    for(i in thisOplModel.I){
        var activity = 0;
        for(a in thisOplModel.A){
            if (thisOplModel.X[i][a] == 1){
                activity = a;
                outputFile.writeln(activity);
            }
        }
        if (activity == 0) outputFile.writeln(activity);
     }
	outputFile.close();
}