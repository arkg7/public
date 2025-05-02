package maze;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        String in = "";
        System.out.print("Enter the dimensions of the maze n x m\nn = ");
        int n = scan.nextInt();
        System.out.print("m = ");
        int m = scan.nextInt();
        Maze maze = new Maze(n, m);
        scan.nextLine();
        while(!in.equals("exit")){
            System.out.println("Enter \"new\" for a new maze, \"print\" to output the maze, \"path\" to get the path to the exit \"printpath\" to print out the path, and \"exit\" to stop.");
            in = scan.nextLine();
            if(in.equals("new")){
                System.out.print("Enter the dimensions of the maze n x m\nn = ");
                n = scan.nextInt();
                System.out.print("m = ");
                m = scan.nextInt();
                maze = new Maze(n, m);            
                scan.nextLine();
            }else if(in.equals("print")){
                System.out.println(maze.toString());
            }else if(in.equals("path")){
                System.out.println(maze.getPath()+"\n");
            }else if(in.equals("printpath")){
                System.out.println(maze.printPath());
            }else if(in.equals("printraw")){
                System.out.println(maze.raw());
            }

        }
        scan.close();
    }
}
