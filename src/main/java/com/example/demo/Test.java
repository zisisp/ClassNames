package com.example.demo;

import java.util.ArrayList;

public class Test {
    public static void main(String[] args) {
        long[][] inventory = new long[3][4];
        long[][] attempts = new long [3][2];
        inventory[0] = getLongValue(0,1,100,2);
        inventory[1] = getLongValue(0,2,50,1);
        attempts[0] = getLongValue(0, 1);
        attempts[1] = getLongValue(0, 1);
        attempts[2] = getLongValue(0, 0);
        Solution a=new Solution();
        long solution = a.solution(inventory, attempts);
        System.out.println("solution = " + solution);


    }

    private static long[] getLongValue(int... args) {
        long[] toReturn = null;
        if (args.length == 4) {
            toReturn = new long[4];
            toReturn[0] = args[0];
            toReturn[1] = args[1];
            toReturn[2] = args[2];
            toReturn[3] = args[3];
        } else if (args.length == 2) {
            toReturn = new long[2];
            toReturn[0] = args[0];
            toReturn[1] = args[1];
        }
        return toReturn;
    }

    static class VendingItem {
        int column;
        int row;
        int costCents;
        int remainingQuantity;

        VendingItem(long[] arr) {
            this.column = (int) arr[0];
            this.row = (int) arr[1];
            this.costCents = (int) arr[2];
            this.remainingQuantity = (int) arr[3];
        }
    }

    static class Solution {
        public long solution(long[][] inventory, long[][] purchaseAttempts) {
            ArrayList<VendingItem> items = new ArrayList();
            for (long[] item : inventory) {
                items.add(new VendingItem(item));
            }

            int columnCount = items.stream().mapToInt(item -> item.row).reduce(0, (acc, item) -> Math.max(acc, item)) + 1;
            int rowCount = items.stream().mapToInt(item -> item.column).reduce(0, (acc, item) -> Math.max(acc, item)) + 1;

            VendingItem[][] grid = new VendingItem[columnCount][rowCount];
            for (VendingItem item : items) {
                grid[item.column][item.row] = item;
            }

            for (long[] purchaseCoordinate : purchaseAttempts) {
                if (purchaseCoordinate.length != 2) {
                    continue;
                }

                int purchaseColumn = (int) purchaseCoordinate[0];
                int purchaseRow = (int) purchaseCoordinate[1];
                if (grid.length <= purchaseColumn) {
                    continue;
                }

                VendingItem[] column = grid[purchaseColumn];
                if (column == null || column.length <= purchaseRow) {
                    continue;
                }

                VendingItem item = column[purchaseRow];
                item.remainingQuantity--;
            }

            int sum = 0;
            for (VendingItem item : items) {
                sum += item.remainingQuantity * item.costCents;
            }

            return sum;
        }
    }
}