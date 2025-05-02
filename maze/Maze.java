package cs3345.maze;

import java.util.Random;

public class Maze {
    private int[][] maze;
    private int[] union;
    private String path;                                                    //Hi.
    private int wid;                                                        //This maze implementation came to me in a vision.
    private int hig;                                                        //Instead of a typical custom class cell approach,
    public Maze(int n, int m){                                              //          I decided to represent the whole maze as a 2D array of integers.
        maze = new int[n][m];
        union = new int[n*m];
        hig = n;
        wid = m;                                                            //Here's the basic idea. Each cell needs to know only 2 things:
        for(int i = 0; i<union.length;i++){                                 //                                      -Which walls are up/down
            union[i] = -1;                                                  //                                      -If the cell is in the path on the way to the exit
        }
        //Disjoint array to keep track of which cells are connected.
        //Bitwise 1 0 0 0 to represent left wall down and 0 0 2 0 
        //to represent right wall.
        //AKA entrance and exit walls.
        maze[0][0] = 8;
        maze[n-1][m-1] = 2;                                                 //Each wall has only 2 states. closed or open. Perhaps even represented by binary 1's and 0's.
        //Randomness to randomize the maze.                                 //Now if we lined up those binary 1's and 0's we could make something like this to represent all the walls to a cell
        Random r = new Random();                                            //    0 0 0 1 == only first wall down       0 0 1 0 == second wall down.   you get it.
        //Until every cell is connected to the entrance                     //          Therefore, we can wrap these binary wall states around the cell in a clocklike fashion
        while(union[0]!=(n*m*-1)){                                          //          to make something like:             0
            //Picks random root cell from disjoint array                    //                                          0       0
            int cell = r.nextInt(n*m);                                      //                                              0
            while(union[cell]>=0){                                          //          Where the most significant bit is the left one, and the top one is the least.
                cell = r.nextInt(n*m);                                      //          Now. Four bits can be represented as a normal integer.
            }                                                               //          This is how the integer in each index of the array represents the status of all walls pertinent to the cell.
            //Picks random side                                             //          We can also use a 5th bit to represent the cell being on the path to the exit.
            int side = r.nextInt(4);                                  //          Unwrapped:     1           1         0             0         1
            //Randomizes side until is valid for chosen cell                //          Meaning:    On Path   Left Wall  Right wall   Right wall   Top wall
            while(!isValid(cell, side)){                                    
                side = r.nextInt(4);                                  
            }                                                               
            //Converts side to power of 2 for binary representation         
            side = (int)Math.pow(2, side);                                
            switch (side) {                                                 
                //Each case follows same idea.
                //Break reverse walls on the cells which have been chosen to be connected.
                //If current cell is less than the parent cell of the cell it is being connected to, it becomes the new parent of the tree.
                //If current cell is greater than the parent of the cell it is being connected to, it becomes the child of the node it is connecting to.
                //Ensuring that the cell with the least value becomes the parent of the tree makes it so every merge gets one step closer to the entrance being the root of the maze tree.
                case 1 -> {                                                
                    if(getParent(cell)!=getParent(cell-m)){
                        maze[cell/m][cell%m] |= 1;
                        maze[(cell-m)/m][cell%m] |= 4;
                        if(cell<getParent(cell-m)){
                            union[cell] = union[getParent(cell-m)]+union[cell];
                            union[getParent(cell-m)] = cell;
                        }else{
                            union[getParent(cell-m)] = union[getParent(cell-m)]+union[cell];
                            union[cell] = cell-m;
                        }
                    }
                }
                case 2 -> {
                    if(getParent(cell)!=getParent(cell+1)){
                        maze[cell/m][cell%m] |= 2;
                        maze[cell/m][(cell+1)%m] |= 8;
                        if(cell<getParent(cell+1)){
                            union[cell] = union[getParent(cell+1)]+union[cell];
                            union[getParent(cell+1)] = cell;
                        }else{
                            union[getParent(cell+1)] = union[getParent(cell+1)]+union[cell];
                            union[cell] = cell+1;
                        }
                    }
                }
                case 4 -> {
                    if(getParent(cell)!=getParent(cell+m)){
                        maze[cell/m][cell%m] |= 4;
                        maze[(cell+m)/m][cell%m] |= 1;
                        if(cell<getParent(cell+m)){
                            union[cell] = union[getParent(cell+m)]+union[cell];
                            union[getParent(cell+m)] = cell;
                        }else{
                            union[getParent(cell+m)] = union[getParent(cell+m)]+union[cell];
                            union[cell] = cell+m;
                        }
                        
                    }
                }
                case 8 -> {
                    if(getParent(cell)!=getParent(cell-1)){
                        maze[cell/m][cell%m] |= 8;
                        maze[cell/m][(cell-1)%m] |= 2;
                        if(cell<getParent(cell-1)){
                            union[cell] = union[getParent(cell-1)]+union[cell];
                            union[getParent(cell-1)] = cell;
                        }else{
                            union[getParent(cell-1)] = union[getParent(cell-1)]+union[cell];
                            union[cell] = cell-1;
                        }
                    }
                }
            }
        }
    }
    //Moves through the disjoint array to get the parent of the input node's tree.
    private int getParent(int cell){
        int index;
        int parent = union[cell];
        if(parent<0){
            return cell;
        }else{
            do{
                index = parent;
                parent = union[parent];
            }while(parent>=0);
        }
        
        return index;
    }
    //Checks to see if the selected wall is valid to be broken based on the size of the maze, selected cell, and selected side.
    private boolean isValid(int cell, int side){
        //If at top, cannot break top wall.
        if(cell/wid==0&&side==0){
            return false;
        }
        //If at right, cannot break right wall.
        if(cell%wid==wid-1&&side==1){
            return false;
        }
        //If at bot, cannot break bot wall.
        if(cell/wid==hig-1&&side==2){
            return false;
        }
        //If at left, cannot break left wall.
        if(cell%wid==0&&side==3){
            return false;
        }else{
            return true;
        }
        //If there is no reason not to, then it must be fine.
    }

    //Typical toString.
    public String toString(){
        String out = "";
            for(int i: maze[0]){
                out+=(" _");
            }
            for(int[] i: maze){
                out+="\n";
                for(int j: i){
                    //just kidding.
                    //Uses bitwise or operator to convert the integer into visual representations.
                    //Each cell is only responsible for printing out it's own left and bot side.
                    //If integer_a | integer_b is equal to integer_a, then the bits present in b were already present in a, meaning that we can select which walls we check for.
                    // |12 is | 1 1 0 0, or left and bot wall down
                    if((j|12)==j){
                        out+=("  ");
                    // |4 is | 0 1 0 0, or just bot wall down
                    }else if ((j|4)==j) {
                        out+=("| ");
                    // |8 is | 1 0 0 0, or just left wall down
                    }else if ((j|8)==j) {
                        out+=(" _");
                    }else{
                        out+=("|_");
                    }
                }
                //Print out right wall, unless the cell says not to, which only happens for the exit because not having a right wall isnt valid for every other cell.
                if((i[wid-1]|2)!=i[wid-1]){
                    out+=("|\t\t");
                }else{
                    out+=("\t\t");
                }
            }
            out+="\n";
        return out;
    }

    //True/false function used to determine whether or not the getPath function can move in a direction based on the state of the walls of the cell.
    private boolean canMove(int cell,int side){
        if((maze[cell/wid][cell%wid]|side)==maze[cell/wid][cell%wid]){
            return true;
        }
        return false;
    }

    //Gets the path by recursively going through the array. Totally DFS. Probably.
    public String getPath(){
        if(path!=null){
            return path;
        }
        String out = " ";
        int cell = 0;
        for(int i = 1; i<=2;i++){
            int side = (int)Math.pow(2, i);
            if(canMove(cell, side)){
                if(side == 2){
                    out = getPath(cell+1, side, "E");
                }else if(side == 4&&out.charAt(0)!='+'){
                    out = getPath(cell+wid, side, "S");
                }
            }
        }
        path = out.substring(1);
        return path;
    }
    //The recursive part :)
    private String getPath(int cell, int from, String in){
        String out = in;
        if(cell==hig*wid-1){
            return "+"+in;
        }else{
            for(int i = 0; i<=3;i++){
                int side = (int)Math.pow(2, i);
                if(canMove(cell, side)){
                    if(side == 1&&from!=4){
                        out = getPath(cell-wid, side, in+"N");
                    }else if(side == 2&&from!=8){
                        out = getPath(cell+1, side, in+"E");
                    }else if(side == 4&&from!=1){
                        out = getPath(cell+wid, side, in+"S");
                    }else if(side == 8&&from!=2){
                        out = getPath(cell-1, side, in+"W");
                    }
                }
                if(out.charAt(0)==('+')){
                    return out;
                }
            }
        }
        
        return out;
    }
    //After obtaining the path, it is scanned and the fifth bit is added to all cells in the path to the exit.
    //Each cell can then be checked like the toString function, and if the fifth bit is present, it means to add a little marker to denote the path for human eyes.
    //It can also be observed that the path can be seen in the raw version just by looking for numbers greater than or equal to 16.
    public String printPath(){
        String out = "";
        getPath();
        int cell = 0;
        for(int i = 0;i<path.length();i++){
            maze[cell/wid][cell%wid] |= 16;
            if(path.charAt(i)=='N'){
                cell -=wid;
            }else if(path.charAt(i)=='E'){
                cell +=1;
            }else if(path.charAt(i)=='S'){
                cell +=wid;
            }else if(path.charAt(i)=='W'){
                cell -=1;
            }
        }
        maze[cell/wid][cell%wid] |= 16;
        for(int i: maze[0]){
            out+=(" _");
        }
        for(int[] i: maze){
            out+="\n";
            for(int j: i){
                if((j|28)==j){
                    out+=(" +");
                }else if ((j|12)==j) {
                    out+=("  ");
                }else if ((j|20)==j) {
                    out+=("|+");
                }else if ((j|4)==j) {
                    out+=("| ");
                }else if ((j|24)==j){
                    out+=(" ±");
                }else if ((j|8)==j) {
                    out+=(" _");
                }else if ((j|16)==j) {
                    out+=("|±");
                }else{
                    out+=("|_");
                }
            }
            if((i[wid-1]|2)!=i[wid-1]){
                out+=("|\t\t");
            }else{
                out+=("\t\t");
            }
            out += "You can also type \"printraw\" to see the maze as its true form.";
        }
        out += "\n";
        return out;
    }

    //Prints out the array in its raw, intense form.
    public String raw(){
        String out = "";
        for(int[] i: maze){
            for(int j: i) {
                out += (j+"\t");
            }
            out += "\n";
        }
        return out;
    }
}
