package com.rts.commonutils_2_0.format.number;


/**
 * 
 * @author HNJ
 *
 */
public class DecimalFomatter {


	/**
	 * This will return a decimal format String without rounding of the number to next decimal place.<p>
	 * E.g., For two decimal place<p>
	 * 5 ==> 5.00<p>
	 * 5.1 ==> 5.10<p>
	 * 5.36978 ==> 5.36<p>
	 * 
	 * <b>Calling: </b><p>
	 * <ol>
	 * 		String result = formatDecimalStringRightWithoutRounding("34.67568, 3);
	 * </ol>
	 * 
	 * @param sourceString
	 * @param numberOfDecimalPointView
	 */
	public String formatDecimalStringRightWithoutRounding(String sourceString, int numberOfDecimalPointView) {
		String preTargetString = "";
		String postTargetString = "";
		String targetString = "";

		boolean isDecimal = false;
		try {
			preTargetString = sourceString.substring(0, sourceString.indexOf('.'));
			postTargetString = sourceString.substring(sourceString.indexOf('.') + 1);

			//Toast.makeText(getApplicationContext(), "Pre: " + targetDataPre + "  //  Post: " + targetDataPost, Toast.LENGTH_LONG).show();
			isDecimal = true;
		}
		catch(Exception e) {
			e.printStackTrace();
			isDecimal = false;
		}

		try {
			//If String is a whole number
			if(!isDecimal) {
				//Log.d("----------------", "whole number");
				String tempConcat = ".";
				for(int i = 0; i < numberOfDecimalPointView; i++) {
					tempConcat = tempConcat + "0";
				}
				targetString = sourceString + tempConcat;
			}
			//If String is a decimal number
			else {
				//If decimal place is less than the required decimal place 
				if(postTargetString.length() < numberOfDecimalPointView) {
					//Log.d("----------------", "targetDataPost.length() < numberOfDecimalPointView");
					String tempConcat = "";
					for(int i = postTargetString.length(); i < numberOfDecimalPointView; i++) {
						tempConcat = tempConcat + "0";
					}

					targetString = sourceString + tempConcat;
				}
				//If decimal place count is exact the number of decimal required
				else if(postTargetString.length() == numberOfDecimalPointView) {
					//Log.d("----------------", "targetDataPost.length() == numberOfDecimalPointView");
					targetString = sourceString;
				}
				else if(postTargetString.length() > numberOfDecimalPointView) {
					//Log.d("----------------", "targetDataPost.length() > numberOfDecimalPointView");
					postTargetString = postTargetString.substring(0, numberOfDecimalPointView);
					targetString = preTargetString + "." + postTargetString;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//Toast.makeText(getApplicationContext(), targetDataSet, Toast.LENGTH_LONG).show();

		return targetString;
	}

}
