package edu.iastate.cs228.hw4;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Stack;
import java.util.HashMap;
import java.util.HashSet;

/**
 * 
 * @author Hilda Hashemi
 *
 */

public class MsgTree {
	public char payloadChar;
	public MsgTree left;
	public MsgTree right;
	// Need static char idx to the tree string for recursive solution
	private static int staticCharIdx = 0;
	private static MsgTree root;
	private static HashMap<Character, Integer> charAndCode;

	// Constructor building the tree from a string
	public MsgTree(String encodingString) {
		charAndCode = new HashMap<>();
		if (encodingString == null || encodingString.length() < 2)
			return;
		root = buildNewMsgTree(encodingString);
	}

	// Constructor for a single code with null children
	public MsgTree(char payloadChar) {
		this.payloadChar = payloadChar;
		this.left = null;
		this.right = null;
	}

	// method to print characters and their binary codes
	public static void printCodes(MsgTree root, String code) {
		if (root == null) {
			System.out.println("Tree is empty.");
			return;
		}
		System.out.println("character code");
		System.out.println("-------------------------");
		StringBuilder path = new StringBuilder();
		preorderTraversal(root, path);
	}

	// It would print the decoded message to the console.
	public static String decode(MsgTree codes, String msg) {
		System.out.println("MESSAGE:");
		MsgTree current = codes;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < msg.length(); i++) {
			if (msg.charAt(i) == '0' && current.left != null)
				current = current.left;
			else if (msg.charAt(i) == '1' && current.right != null)
				current = current.right;
			if (current.left == null && current.right == null) {
				sb.append(current.payloadChar);
				current = codes;
			}
		}
		System.out.println(sb.toString());
		return sb.toString();
	}

	private MsgTree buildNewMsgTree(String encodingString) {
		// The following seven lines are the recursive method.
//		char c = encodingString.charAt(staticCharIdx++);
//		MsgTree tree = new MsgTree(c);
//		if (c != '^')
//			return tree;
//		tree.left = buildNewMsgTree(encodingString);
//		tree.right = buildNewMsgTree(encodingString);
//		return tree;

		// The following code is non-recursive.
		// If none of the left and right has been visited
		HashSet<MsgTree> black = new HashSet<>();
		// If left is visited
		HashSet<MsgTree> grey = new HashSet<>();
		Stack<MsgTree> stk = new Stack<>();
		MsgTree nodeRoot = new MsgTree(encodingString.charAt(staticCharIdx++));
		black.add(nodeRoot);
		stk.push(nodeRoot);
		while (staticCharIdx < encodingString.length()) {
			MsgTree node = new MsgTree(encodingString.charAt(staticCharIdx++));
			MsgTree stkHead = stk.peek();
			if (black.contains(stkHead)) {
				stkHead.left = node;
				black.remove(stkHead);
				grey.add(stkHead);
			} else {
				stkHead.right = node;
				grey.remove(stkHead);
				stk.pop();
			}

			if (node.payloadChar == '^') {
				stk.add(node);
				black.add(node);
			}

		}
		return nodeRoot;
	}

	private static void preorderTraversal(MsgTree root, StringBuilder path) {
		if (root.left == null && root.right == null) {
			if (root.payloadChar != '\n')
				System.out.println("   " + root.payloadChar + "      " + path.toString());
			else
				System.out.println("   " + "\\n" + "     " + path.toString());
			charAndCode.put(root.payloadChar, path.toString().length());
		}
		if (root.left != null) {
			preorderTraversal(root.left, path.append('0'));
			// When done with left, back track the path.
			path.setLength(path.length() - 1);
		}
		if (root.right != null) {
			preorderTraversal(root.right, path.append('1'));
			// When done with right, back track the path.
			path.setLength(path.length() - 1);
		}
	}

	public static void main(String args[]) {
		System.out.println("Please enter filename to decode: ");
		Scanner sc = new Scanner(System.in);
		String fileName = sc.next();
		try {
			File f = new File(fileName);
			sc = new Scanner(f);
			int count = 0;
			String scheme = sc.nextLine();
			String archivedMsg = sc.nextLine();
			if (sc.hasNextLine()) {
				scheme += '\n' + archivedMsg;
				archivedMsg = sc.nextLine();
			}
			MsgTree tree = new MsgTree(scheme);
			printCodes(tree.root, archivedMsg);
			String msg = decode(tree.root, archivedMsg);
			// Extra credit statistics
			System.out.println("STATISTICS:");
			int totalBits = 0;
			for (int i = 0; i < msg.length(); i++) {
				totalBits += charAndCode.get(msg.charAt(i));
			}
			System.out.println("Avg  bits/char:" + "        " + (double) totalBits / msg.length());
			System.out.println("Total characters:" + "      " + msg.length());
			System.out.println(
					"Space savings:" + "         " + (1 - (double) archivedMsg.length() / (msg.length() * 16)) * 100);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
}
