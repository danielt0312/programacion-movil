package com.z_iti_271304_u3_e02;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KarnaughCanvas extends View {

    private int numVariables;
    private String[] outputs;
    private Paint cellPaint;
    private Paint textPaint;
    private Paint groupPaint;
    private Paint labelPaint;
    private int rows, cols;
    private Random random;

    public KarnaughCanvas(Context context, int numVariables, String[] outputs) {
        super(context);
        this.numVariables = numVariables;
        this.outputs = outputs;
        this.cellPaint = new Paint();
        this.textPaint = new Paint();
        this.groupPaint = new Paint();
        this.labelPaint = new Paint();
        this.rows = (int) Math.pow(2, numVariables / 2);
        this.cols = (int) Math.pow(2, (numVariables + 1) / 2);
        this.random = new Random();

        // Configurar los estilos de los pinceles
        cellPaint.setStyle(Paint.Style.STROKE);
        cellPaint.setColor(Color.BLACK);

        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30);
        textPaint.setTextAlign(Paint.Align.CENTER);

        groupPaint.setStyle(Paint.Style.STROKE);
        groupPaint.setStrokeWidth(3);

        labelPaint.setColor(Color.BLACK);
        labelPaint.setTextSize(25);
        labelPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int cellWidth = getWidth() / (cols + 1);
        int cellHeight = getHeight() / (rows + 4);

        String cornerLabel = formatVariableLabel(numVariables);
        canvas.drawText(cornerLabel, cellWidth * 0.7f, cellHeight * 0.7f, labelPaint);

        for (int col = 0; col < cols; col++) {
            String label = getGrayCodeString(col, (numVariables + 1) / 2);
            canvas.drawText(label, (col + 1) * cellWidth + cellWidth / 2, cellHeight * 0.7f, labelPaint);
        }

        for (int row = 0; row < rows; row++) {
            String label = getGrayCodeString(row, numVariables / 2);
            canvas.drawText(label, cellWidth * 0.4f, (row + 1) * cellHeight + cellHeight / 2, labelPaint);
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int left = (j + 1) * cellWidth;
                int top = (i + 1) * cellHeight;
                int right = left + cellWidth;
                int bottom = top + cellHeight;

                canvas.drawRect(left, top, right, bottom, cellPaint);

                int grayRow = binaryToGray(i);
                int grayCol = binaryToGray(j);
                int mintermIndex = (grayRow << ((numVariables + 1) / 2)) | grayCol;

                String value = (mintermIndex < outputs.length) ? outputs[mintermIndex] : "-";
                canvas.drawText(value, (left + right) / 2, (top + bottom) / 2 + 10, textPaint);
            }
        }

        boolean[][] visited = new boolean[rows][cols];
        findAndDrawGroups(canvas, visited, cellWidth, cellHeight);
    }

    private void findAndDrawGroups(Canvas canvas, boolean[][] visited, int cellWidth, int cellHeight) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int mintermIndex = getIndex(i, j);

                if ((outputs[mintermIndex].equals("1") || outputs[mintermIndex].equals("X"))) {
                    // Intentar formar grupos de tamaños que sean potencias de 2, empezando por el más grande posible
                    for (int groupSize = 4; groupSize >= 1; groupSize /= 2) {
                        if (findAndDrawOptimalGroup(canvas, visited, i, j, cellWidth, cellHeight, groupSize)) {
                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean findAndDrawOptimalGroup(Canvas canvas, boolean[][] visited, int row, int col, int cellWidth, int cellHeight, int groupSize) {
        List<int[]> groupCells = findGroupOfSize(row, col, groupSize, visited);

        if (!groupCells.isEmpty()) {
            drawGroup(groupCells, canvas, cellWidth, cellHeight);
            markGroupAsVisited(groupCells, visited);
            return true;
        }

        return false;
    }

    public int getRows() {
        return this.rows;
    }

    public int getCols() {
        return this.cols;
    }

    public int getIndex(int row, int col) {
        return (binaryToGray(row) << ((int) (Math.log(cols) / Math.log(2)))) | binaryToGray(col);
    }

    public List<int[]> findGroupOfSize(int row, int col, int groupSize, boolean[][] visited) {
        List<int[]> groupCells = new ArrayList<>();
        if (groupSize == 1) {
            checkPattern(row, col, 1, 1, groupCells, visited);
        } else if (groupSize == 2) {
            checkPattern(row, col, 2, 1, groupCells, visited);
            checkPattern(row, col, 1, 2, groupCells, visited);
        } else if (groupSize == 4) {
            checkPattern(row, col, 2, 2, groupCells, visited);
            checkFullRow(row, groupCells, visited);
            checkFullColumn(col, groupCells, visited);
        } else if (groupSize == 8) {
            checkPattern(row, col, 4, 2, groupCells, visited);
            checkPattern(row, col, 2, 4, groupCells, visited);
            checkPattern(row, col, 4, 4, groupCells, visited);
            checkPattern(row, col, 8, 2, groupCells, visited);
            checkPattern(row, col, 2, 8, groupCells, visited);
            checkPattern(row, col, 8, 4, groupCells, visited);
            checkPattern(row, col, 4, 8, groupCells, visited);
            checkPattern(row, col, 8, 8, groupCells, visited);
        } else if (groupSize == 16) {
            checkPattern(row, col, 8, 8, groupCells, visited);
            checkPattern(row, col, 16, 8, groupCells, visited);
            checkPattern(row, col, 8, 16, groupCells, visited);
            checkPattern(row, col, 16, 16, groupCells, visited);

            checkFullRow(row, groupCells, visited);
            checkFullColumn(col, groupCells, visited);
        }
        return groupCells;
    }

    public void markGroupAsVisited(List<int[]> groupCells, boolean[][] visited) {
        for (int[] cell : groupCells) {
            int row = cell[0];
            int col = cell[1];
            visited[row][col] = true;
        }
    }

    public String createTermFromGroup(List<int[]> groupCells, int numVariables) {
        boolean[] rowBits = new boolean[numVariables / 2];
        boolean[] colBits = new boolean[(numVariables + 1) / 2];
        boolean[] rowDontCare = new boolean[numVariables / 2];
        boolean[] colDontCare = new boolean[(numVariables + 1) / 2];

        for (int i = 0; i < rowBits.length; i++) {
            rowDontCare[i] = true;
        }
        for (int i = 0; i < colBits.length; i++) {
            colDontCare[i] = true;
        }

        int[] firstCell = groupCells.get(0);
        int grayRow = binaryToGray(firstCell[0]);
        int grayCol = binaryToGray(firstCell[1]);

        for (int[] cell : groupCells) {
            int currentGrayRow = binaryToGray(cell[0]);
            int currentGrayCol = binaryToGray(cell[1]);

            for (int i = 0; i < rowBits.length; i++) {
                if (((grayRow >> i) & 1) != ((currentGrayRow >> i) & 1)) {
                    rowDontCare[i] = false;
                }
            }
            for (int i = 0; i < colBits.length; i++) {
                if (((grayCol >> i) & 1) != ((currentGrayCol >> i) & 1)) {
                    colDontCare[i] = false;
                }
            }
        }

        StringBuilder termBuilder = new StringBuilder();
        String[] variableNames = {"a", "b", "c", "d", "e", "f", "g", "h"};

        for (int i = 0; i < rowBits.length; i++) {
            if (rowDontCare[i]) {
                boolean bitValue = ((grayRow >> i) & 1) == 1;
                termBuilder.append(bitValue ? variableNames[i] : variableNames[i] + "'");
            }
        }

        for (int i = 0; i < colBits.length; i++) {
            if (colDontCare[i]) {
                boolean bitValue = ((grayCol >> i) & 1) == 1;
                termBuilder.append(bitValue ? variableNames[rowBits.length + i] : variableNames[rowBits.length + i] + "'");
            }
        }

        return termBuilder.toString();
    }


    private void checkFullRow(int row, List<int[]> groupCells, boolean[][] visited) {
        boolean validGroup = true;
        boolean containsUnvisited = false;

        for (int col = 0; col < cols; col++) {
            int mintermIndex = getIndex(row, col);
            if (!(outputs[mintermIndex].equals("1") || outputs[mintermIndex].equals("X")) || visited[row][col]) {
                validGroup = false;
                break;
            }
            if (!visited[row][col]) {
                containsUnvisited = true;
            }
        }

        if (validGroup && containsUnvisited) {
            for (int col = 0; col < cols; col++) {
                groupCells.add(new int[]{row, col});
            }
        }
    }

    private void checkFullColumn(int col, List<int[]> groupCells, boolean[][] visited) {
        boolean validGroup = true;
        boolean containsUnvisited = false;

        for (int row = 0; row < rows; row++) {
            int mintermIndex = getIndex(row, col);
            if (!(outputs[mintermIndex].equals("1") || outputs[mintermIndex].equals("X")) || visited[row][col]) {
                validGroup = false;
                break;
            }
            if (!visited[row][col]) {
                containsUnvisited = true;
            }
        }

        if (validGroup && containsUnvisited) {
            for (int row = 0; row < rows; row++) {
                groupCells.add(new int[]{row, col});
            }
        }
    }


    private void checkPattern(int startRow, int startCol, int rowSpan, int colSpan, List<int[]> groupCells, boolean[][] visited) {
        boolean validGroup = true;
        boolean containsUnvisited = false;

        for (int i = 0; i < rowSpan; i++) {
            for (int j = 0; j < colSpan; j++) {
                int currentRow = (startRow + i) % rows;
                int currentCol = (startCol + j) % cols;
                int mintermIndex = getIndex(currentRow, currentCol);

                if (!(outputs[mintermIndex].equals("1") || outputs[mintermIndex].equals("X"))) {
                    validGroup = false;
                    break;
                }
                if (!visited[currentRow][currentCol]) {
                    containsUnvisited = true;
                }
            }
        }

        if (validGroup && containsUnvisited) {
            for (int i = 0; i < rowSpan; i++) {
                for (int j = 0; j < colSpan; j++) {
                    int currentRow = (startRow + i) % rows;
                    int currentCol = (startCol + j) % cols;
                    groupCells.add(new int[]{currentRow, currentCol});
                }
            }
        }
    }



    private void drawGroup(List<int[]> groupCells, Canvas canvas, int cellWidth, int cellHeight) {
        int minRow = rows, maxRow = 0, minCol = cols, maxCol = 0;
        for (int[] cell : groupCells) {
            int row = cell[0];
            int col = cell[1];
            if (row < minRow) minRow = row;
            if (row > maxRow) maxRow = row;
            if (col < minCol) minCol = col;
            if (col > maxCol) maxCol = col;
        }

        boolean wrapRow = (maxRow - minRow + 1) == rows;
        boolean wrapCol = (maxCol - minCol + 1) == cols;

        int left = wrapCol ? cellWidth : (minCol + 1) * cellWidth;
        int top = wrapRow ? cellHeight : (minRow + 1) * cellHeight;
        int right = wrapCol ? (cols + 1) * cellWidth : (maxCol + 2) * cellWidth;
        int bottom = wrapRow ? (rows + 1) * cellHeight : (maxRow + 2) * cellHeight;

        groupPaint.setColor(getRandomColor());
        RectF rect = new RectF(left, top, right, bottom);
        canvas.drawOval(rect, groupPaint);
    }

    private int getRandomColor() {
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }


    private int binaryToGray(int num) {
        return num ^ (num >> 1);
    }

    private String getGrayCodeString(int num, int bits) {
        StringBuilder sb = new StringBuilder();
        int gray = binaryToGray(num);
        for (int i = bits - 1; i >= 0; i--) {
            sb.append(((gray >> i) & 1) == 1 ? "1" : "0");
        }
        return sb.toString();
    }

    private String formatVariableLabel(int numVariables) {
        String[] variableNames = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l"};
        StringBuilder label = new StringBuilder();

        int numColumnVars = (numVariables + 1) / 2;
        int numRowVars = numVariables / 2;

        for (int i = 0; i < numRowVars; i++) {
            label.append(variableNames[i]);
        }

        label.append("\\");

        for (int i = numRowVars; i < numRowVars + numColumnVars; i++) {
            label.append(variableNames[i]);
        }

        return label.toString();
    }
}
