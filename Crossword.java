import java.io.*;
  import java.util.*;
  

public final class Crossword implements WordPuzzleInterface { 
    char[][] theBoard;

    //List<StringBuilder> checkedWord = new LinkedList<StringBuilder>();
    //List<StringBuilder> checkedPrefix = new LinkedList<StringBuilder>();

    final boolean debug = false;
    final boolean runtime = false;
	/*
     * fills out a word puzzle defined by an empty board. 
     * The characters in the empty board can be:
     *    '+': any letter can go here
     *    '-': no letter is allowed to go here
     *     a letter: this letter has to remain in the filled puzzle
     *  @param board is a 2-d array representing the empty board to be filled
     *  @param dictionary is the dictinary to be used for filling out the puzzle
     *  @return a 2-d array representing the filled out puzzle
     */
    public char[][] fillPuzzle(char[][] board, DictInterface dictionary){
      long startTime = System.currentTimeMillis();
      int size = board.length;
      if(size==0) return board;
      char[][] filledBoard = new char[size][size];
      for(int r=0;r<size;r++){
        for(int c=0;c<size;c++){
          filledBoard[r][c]='+';
        }
      }
      if(solve(board,filledBoard,dictionary,0,0)){
        long endTime = System.currentTimeMillis();
        if(runtime) System.out.println("Runtime: "+((endTime-startTime))+" milliseconds");
        return theBoard;
      }else{
        long endTime = System.currentTimeMillis();
        if(runtime) System.out.println("Runtime: "+((endTime-startTime))+" milliseconds");
        if(debug) System.out.println("No Valid Solution Found");
        return null;
      }
      
      //if(!solve(board,dictionary,rowInd,colInd)) throw new Exception("No Valid Solution Found");

	}
    /*
     * checks if filledBoard is a correct fill for emptyBoard
     * @param emptyBoard is a 2-d array representing an empty board
     * @param filledBoard is a 2-d array representing a filled out board
     * @param dictionary is the dictinary to be used for checking the puzzle
     * @return true if rules defined in fillPuzzle has been followed and 
     *  that every row and column is a valid word in the dictionary. If a row
     *  a column has one or more '-' in it, then each segment separated by 
     * the '-' should be a valid word in the dictionary 
     */
    public boolean checkPuzzle(char[][] emptyBoard, char[][] filledBoard, DictInterface dictionary){
      long startTime = System.currentTimeMillis();
      if(emptyBoard==null || filledBoard == null) return false; // returns false if nulls
      int size = emptyBoard.length;
      if(filledBoard.length!=size) return false; // if filledBoard isnt same size return false


      for(int r=0;r<size;r++){
        for(int c = 0; c<size; c++){
          char emptyTemp = emptyBoard[r][c];
          char filledTemp = filledBoard[r][c];

          if(emptyTemp=='-'&& filledTemp!='-') return false; // if emptyTemp is a - and filledTemp isnt a -, return false
          if(Character.isLetter(emptyTemp)&& emptyTemp!=filledTemp){
            long endTime = System.currentTimeMillis();
            if(runtime) System.out.println("Runtime: "+((endTime-startTime))+" milliseconds");
            return false;
          } //if emptyTemp is a letter  and filledTemp isnt equal, return false
          if(emptyTemp=='+'&&!(Character.isLetter(filledTemp))){
            long endTime = System.currentTimeMillis();
            if(runtime) System.out.println("Runtime: "+((endTime-startTime))+" milliseconds");
            return false; //if emptyTemp is + and filledTemp isnt a letter, return false 
          }
        }
      }
      StringBuilder[] toCheck = getRows(filledBoard);
      for(int i = 0; i<toCheck.length;i++){
        if(!isStringWords(toCheck[i], dictionary)){
          long endTime = System.currentTimeMillis();
          if(runtime) System.out.println("Runtime: "+((endTime-startTime))+" milliseconds");
          return false;
        }
      }
      toCheck = getCols(filledBoard);
      for(int i = 0; i<toCheck.length;i++){
        if(!isStringWords(toCheck[i], dictionary)){
        long endTime = System.currentTimeMillis();
        if(runtime) System.out.println("Runtime: "+((endTime-startTime))+" milliseconds");
        return false;
        }
      }

    long endTime = System.currentTimeMillis();
    if(runtime) System.out.println("Runtime: "+((endTime-startTime))+" milliseconds");
		return true;
	}
  //==============================SOLVE==================================
    /*
     * Recusively solves the 2-d array
     * @param emptyBoard is a 2-d array representing an empty board
     * @param filledBoard is a 2-d array representing a filled out board
     * @param dictionary is the dictinary to be used for checking the puzzle
     * @param represents the starting row index to be solved
     * @param represents the starting column index to be solved
     * @return true if a solution has been found, and copies the solution into global variable theBoard
     */
    private boolean solve(char[][] emptyBoard, char[][] board, DictInterface dictionary, int row, int col){
      int size = board.length;
      int curRow = row;
      int curCol = col;
      int nextRow = row;
      int nextCol = col;
      if(curCol==size-1){ //if current row ind is at end of row
        nextCol=0;
        nextRow = curRow+1;
      }else{ 
        nextCol =curCol+1;
      }
      if(debug) System.out.println("called solve method at " + curRow + ", " + curCol);

      //if emptyboard has letter @ this location
      if(Character.isLetter(emptyBoard[curRow][curCol])){
        board[curRow][curCol]=emptyBoard[curRow][curCol];
        if(debug)System.out.println("letter "+board[curRow][curCol]+" placed @"+curRow+", "+ curCol);
        if(solve(emptyBoard,board,dictionary,nextRow, nextCol)) return true;

        if(curCol == size-1 && curRow == size-1){//if current index is last position of board, check if full solution
          if(debug) System.out.println("reached end of board with prefill at" + curRow + ", " + curCol);
          if(isFullySolved(emptyBoard,board,dictionary)){
            theBoard = board;
            return true;
          } 
        }


      }else{
        for(char place = 'a'; place<='z'; place++){
          //.println("     testing "+place+" @ "+curRow+", "+curCol);
          board[curRow][curCol] = place;

          if(curCol == size-1 && curRow == size-1){//if current index is last position of board, check if full solution
            if(debug) System.out.println("reached end of board at" + curRow + ", " + curCol);
            if(isFullySolved(emptyBoard,board,dictionary)){
              theBoard = board;
              return true;
            } 
          } else if(isValid(emptyBoard,board,dictionary,curRow, curCol)){ //if current letter is valid and not at end of board, increment and call solve on next index
          //increment row and col to next index
            if(debug)System.out.println("solve @"+curRow+", "+ curCol);
            if(solve(emptyBoard,board,dictionary,nextRow, nextCol)) return true;
          }
        }
      }
      board[curRow][curCol] = emptyBoard[curRow][curCol];
      return false;
    }

      //===========================ISVALID===========================================
    /*
     * Checks if a given semi-filled board is a potential valid solution. Checks if semi filled rows or columns are valid prefixes, and filled rows or columns are valid words 
     * @param emptyBoard is a 2-d array representing an empty board
     * @param board is a 2-d array representing a filled out board
     * @param dictionary is the dictinary to be used for checking the puzzle
     * @param row is an int representing the row number of the recently placed index
     * @param col is an int representing the column number of the recently placed index
     * @return true if the board is a valid partial solution, false if not
     */
  private boolean isValid(char[][] emptyBoard, char[][] board, DictInterface dictionary, int row, int col){
    int size = board.length;
    StringBuilder curRow = getRow(board,row);
    StringBuilder curCol = getCol(board,col);

    //check rows
    if(debug)System.out.print("     Test isValid cols for ");
    //remove all + and - from row string
    String str = curRow.toString();
    str = str.replaceAll("\\+","");
    str = str.replaceAll("-","");
    if(debug) System.out.print(" row:"+str);
    StringBuilder sb = new StringBuilder(str);
    boolean isPrevFill = false;
    /* 
        if(sb.length()>=size && checkedWord.size()!=0){
          for(int j = 0; j<checkedWord.size();j++){
            if(checkedWord.get(j).equals(sb)) isPrevFill = true;
          }
        }if(sb.length()<size && checkedWord.size()!=0){
          for(int j = 0; j<checkedPrefix.size();j++){
            if(checkedPrefix.get(j).equals(sb)) isPrevFill = true;
          }
        }
        */
    if(!isPrevFill){
      if(sb.length()>1){
        int searchVal = dictionary.searchPrefix(sb);

        if(sb.length() >= size && !(searchVal==2||searchVal==3)){ //if length is same as board length it must be a word
          //checkedWord.add(sb);
          if(debug)System.out.println("          Failed isValid in row sb>=size, searchVal = "+searchVal);
            return false;
          }else if(sb.length() < size && !(searchVal==1||searchVal ==3)){//if length is shorter than board length it must be a prefix
            //checkedPrefix.add(sb);
            if(debug)System.out.println("          Failed isValid in sb<size, searchVal = "+searchVal);
            return false;
          }
      }
    }
    //remove all + and - from checked string
    str = curCol.toString();
    str = str.replaceAll("\\+","");
    str = str.replaceAll("-","");
    if(debug)System.out.print(" col:"+str);
    sb = new StringBuilder(str);
    //==============
    /*
    isPrevFill = false;
    if(sb.length()>=size && checkedWord.size()!=0){
      for(int j = 0; j<checkedWord.size();j++){
        if(checkedWord.get(j).equals(sb)) isPrevFill = true;
      }
    }if(sb.length()<size && checkedWord.size()!=0){
      for(int j = 0; j<checkedPrefix.size();j++){
        if(checkedPrefix.get(j).equals(sb)) isPrevFill = true;
      }
    }
    */
    //===============
    if(!isPrevFill){
      if(sb.length()>1){
        int searchVal = dictionary.searchPrefix(sb);
        if(sb.length() == size && searchVal<2){ //if length is same as board length it must be a word
          return false;
        }else if(sb.length() <= size && !(searchVal==1||searchVal ==3)){//if length is shorter than board length it must be a prefix
          return false;
        }
      }
    }

    if(debug)System.out.println("\n");
    return true;

  }
  //===============================ISFULLYSOLVED==============================================
   /*
     * Checks if a given filled board is a solution. Checks if rows and columns are valid words
     * @param emptyBoard is a 2-d array representing an empty board
     * @param filledBoard is a 2-d array representing a filled out board
     * @param dictionary is the dictinary to be used for checking the puzzle
     * @return true if the board is a fully solved solution, false if not
     */
  private boolean isFullySolved(char[][] emptyBoard, char[][] filledBoard, DictInterface dictionary){
    StringBuilder[] rowsToCheck = getRows(filledBoard);
    StringBuilder[] colsToCheck = getCols(filledBoard);
    for(int r=0;r<filledBoard.length;r++){
      for(int c=0;c<filledBoard.length;c++){
        if(!(Character.isLetter(filledBoard[r][c]))){
          if(debug)System.out.println("board reached last index with empty space @index " + r +", "+c);
          return false;
        }
      }
    }
    for(int i = 0; i<rowsToCheck.length;i++){
      if(rowsToCheck[i].length()!=0){
        int searchVal = dictionary.searchPrefix(rowsToCheck[i]);
        if(searchVal<2) return false;
        searchVal = dictionary.searchPrefix(colsToCheck[i]);
        if(searchVal<2) return false;
      }
    }
    return true;


  }


  //===============================ISSTRINGWORDS================================================
  /*
     * Checks if a given row or column is a valid solution. Breaks the line at "-" characters and checks validity of all words more than 1 letter
     * @param emptyBoard is a 2-d array representing an empty board
     * @param filledBoard is a 2-d array representing a filled out board
     * @param dictionary is the dictinary to be used for checking the puzzle
     * @return true if the StringBuilder consists of valid words, false if not
     */
  private boolean isStringWords(StringBuilder toCheck, DictInterface dictionary){
    String str =  toCheck.toString();
    String[] stringsToCheck =  str.split("-");
    for(int i = 0; i<stringsToCheck.length;i++){
      StringBuilder temp = new StringBuilder(stringsToCheck[i]);
      if(temp.length()>1){
        int searchVal = dictionary.searchPrefix(temp);
        if(temp.length()<=1 && searchVal<=1){
          if(debug)System.out.println("Word \""+temp+"\" rejected as "+searchVal);
          return false;
        }
      }
    }
    return true;
  }

  //===============================GETROWSANDCOLS================================================
  /*
     * given a board, gives a StringBuilder array representing the characters in row order for the board
     * @param board is a 2-d array representing a board
     * @return StringBuilder[] with the characters of each row in the board
     */
  private StringBuilder[] getRows(char[][] board){
    int size = board.length;
    StringBuilder[] rowArray = new StringBuilder[size];
    for(int i = 0; i<rowArray.length;i++){
      rowArray[i]= new StringBuilder("");
    }
    for(int r=0;r<size;r++){
      for(int c=0;c<size;c++){
        rowArray[r].append(board[r][c]);
      }
    }
    return rowArray;
  }
  
/*
     * given a board, gives a StringBuilder array representing the characters in column order for the board
     * @param board is a 2-d array representing a board
     * @return StringBuilder[] with the characters of each column in the board
     */
  private StringBuilder[] getCols(char[][] board){
    int size = board.length;
    StringBuilder[] colArray = new StringBuilder[size];
    for(int i = 0; i<colArray.length;i++){
      colArray[i]= new StringBuilder("");
    }
    for(int c=0;c<size;c++){
      for(int r=0;r<size;r++){
        colArray[c].append(board[r][c]);
      }
    }
    return colArray;
  }

  //====================== GETROW AND GETCOL=========================
  
  /*
     * given a board and row number, gives a StringBuilder representing the characters this row for the board
     * @param board is a 2-d array representing a board
     * @param row is an int representing the row number
     * @return StringBuilder with the characters the selected row in the board
     */
  private StringBuilder getRow(char[][] board,int row){
    int size = board.length;
    StringBuilder rowString = new StringBuilder("");
    for(int c = 0; c<size;c++){
      rowString.append(board[row][c]);
    }
    return rowString;
  }
 /*
     * given a board and column number, gives a StringBuilder representing the characters this column for the board
     * @param board is a 2-d array representing a board
     * @param col is an int representing the column number
     * @return StringBuilder with the characters the selected column in the board
     */
  private StringBuilder getCol(char[][] board,int col){
    int size = board.length;
    StringBuilder colString = new StringBuilder("");
    for(int r = 0; r<size;r++){
      colString.append(board[r][col]);
    }
    return colString;
  }
    
    
    
    
    
}


 



  
