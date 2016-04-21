/**
 * 
 */
package testsFromLectures;

/**
 * @author Steven Kenyon
 * Selection sort created from general description
 */
public class SelectionSort {
	private static int[] inputInt;
	public SelectionSort()
	{
		for(int i = 0; i < inputInt.length - 1; i++)
		{
			int smallestFoundNum = inputInt[i];
			int smallestFoundLoc = i;
			boolean shouldSwap = false;
			for(int j = i + 1; j < inputInt.length; j++)//Search for smallest number
			{
				if(smallestFoundNum > inputInt[j]) //Mark when if/when found
				{
					smallestFoundNum = inputInt[j];
					smallestFoundLoc = j;
					shouldSwap = true;
				}
			}
			if(shouldSwap) swap(i, smallestFoundLoc);//swap current with smallest
		}
	}
	/**
	 * Swapping two values from the static int[] inputInt.
	 * 
	 * @param loc1
	 * @param loc2
	 */
	private void swap(int loc1, int loc2)
	{
		int tempHolder = inputInt[loc1];
		inputInt[loc1] = inputInt[loc2];
		inputInt[loc2] = tempHolder;
	}
	
	private void printArrayContents()
	{
		System.out.println("");
		for(int i = 0; i < inputInt.length; i++)
		{
			System.out.print(inputInt[i] + " ");
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		inputInt = new int[10];
		inputInt[0] = 10;
		inputInt[1] = 9;
		inputInt[2] = 8;
		inputInt[3] = 7;
		inputInt[4] = 6;
		inputInt[5] = 5;
		inputInt[6] = 4;
		inputInt[7] = 3;
		inputInt[8] = 2;
		inputInt[9] = 1;
		SelectionSort a = new SelectionSort();
		System.out.println("");
		for(int i = 0; i < inputInt.length; i++)
		{
			System.out.print(inputInt[i] + " ");
		}

	}
}
