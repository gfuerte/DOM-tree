package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	public void build() {
		root = buildRec();
	}
	
	private TagNode buildRec() {
		if (sc.hasNextLine() == false) {
			return null;
		}
		String s = sc.nextLine();
		if (s == null) {
			return null;
		}
		TagNode t = new TagNode(s, null, null);
		if (s.charAt(0) == '<') {
			if (s.charAt(1) == '/') {
				return null;
			} else {
				String s2 = s.substring(1, s.length()-1);
				t = new TagNode(s2, null, null);
			}
		}
		
		if (s.charAt(0) == '<' && s.charAt(1) != '/') {
			t.firstChild = buildRec();
		}
		t.sibling = buildRec();
		
		
		return t;
	}
	
	public void replaceTag(String oldTag, String newTag) {
		if (root == null || oldTag == null || newTag == null) {
			return;
		}
		replaceRec(oldTag, newTag, root);
	}
	
	private void replaceRec(String oldTag, String newTag, TagNode root) {
		if (oldTag == null || newTag == null) {
			return;
		}
		
		if (root == null) {
			return;
		}
		
		if (root.tag.equals(oldTag)) {
			root.tag = newTag;
		}
		
		replaceRec(oldTag, newTag, root.firstChild);
		replaceRec(oldTag, newTag, root.sibling);
	}

	public void boldRow(int row) {
		if (row <= 0) {
			return;
		}
		
		TagNode table = searchTable(root);
		
		if (table == null) {
			return;
		}
		
		TagNode tr = table.firstChild;
		
		
		int count = 1;
		for (int i = count; i < row; i++) {
			tr = tr.sibling;
		}
		
		TagNode td = tr.firstChild;
		
		while (td != null) {
			td.firstChild = new TagNode("b", td.firstChild, null);
			td = td.sibling;
		}
		
	}
	
	private TagNode searchTable(TagNode root) {
		
		if (root == null) {
			return null;
		}
		
		TagNode node = null;
		
		if(root.tag.equals("table")) {
			node =  root;
			return node;
		}
		
		if (node == null) {
			node = searchTable(root.firstChild);
		}
		
		if (node == null) {
			node = searchTable(root.sibling);
		}
		
		return node;
	}
	
	public void removeTag(String tag) {
		if (root == null) {
			return;
		}
		
		removeTagRec(tag, root);
	}
	
	private void removeTagRec(String tag, TagNode root) {	
		if (root == null || tag == null) {
			return;
		}
		
		
		TagNode kid = root.firstChild;
		
		if (tag.equals("p") || tag.equals("em") || tag.equals("b")) {
			
			if(root.tag.equals(tag) && kid != null) {
				root.tag = root.firstChild.tag;
				TagNode tempGrankids = root.firstChild.firstChild;
				
				if(root.firstChild.sibling != null) {
					
					TagNode cur = root.firstChild;
					TagNode tempSib = root.sibling;
					
					while (cur.sibling != null) {
						cur = cur.sibling;
					
					}
					cur.sibling = tempSib;
					root.sibling = root.firstChild.sibling;
					
				}
				root.firstChild = tempGrankids;
			}
			
			removeTagRec(tag, root.firstChild);
			removeTagRec(tag, root.sibling);
			
		} else if (tag.equals("ol") || tag.equals("ul")) {
			
			if(root.tag.equals(tag) && kid != null) {
				
				TagNode cur = root.firstChild;
				TagNode tempSib = root.sibling;
				TagNode tempGrankids = root.firstChild.firstChild;
				TagNode tempCousins = root.firstChild.sibling;
				
				while(cur.sibling != null) {
					cur.tag = "p"; 
					cur = cur.sibling;
				}
				
				root.tag = "p";
				cur.tag = "p";
				cur.sibling = tempSib;
				root.sibling = tempCousins;
				root.firstChild = tempGrankids;
			}
			
			removeTagRec(tag, root.firstChild);
			removeTagRec(tag, root.sibling);
			
		} else {
			
			return;
			
		}
		
		
	}

	public void addTag(String word, String tag) {
		if (root == null) {
			return;
		}
		root = addTagRec(root, word, tag);
	}
	
	private TagNode addTagRec(TagNode root, String word, String tag){
		if (root == null || word == null || tag == null) {
			return null;
		}
		
		root.firstChild = addTagRec(root.firstChild, word, tag);
		root.sibling = addTagRec(root.sibling, word, tag);
		
		if (root.tag.toLowerCase().contains(word.toLowerCase())) {
			if (root.tag.toLowerCase().equals(word.toLowerCase())) {
				TagNode temp = new TagNode(tag, root, null);
				root = temp;
			} else {
				StringTokenizer tokenizer = new StringTokenizer(root.tag, " ");
				String s = "";
				String master = "";
				boolean x = true;
				TagNode ptr = null;
				
				while (tokenizer.hasMoreTokens()) {
					
					s = tokenizer.nextToken();
					
					
					
					if (s.toLowerCase().equals(word.toLowerCase())) {
						if (x == true) {
							if (master.length() > 0) {
								TagNode tempSib = root.sibling;
								TagNode theWord = new TagNode(s, null, null);
								TagNode temp = new TagNode(tag, theWord, null);
								root.tag = master;
								root.sibling = temp;
								temp.sibling = tempSib;
								master = " ";
								x = false;
								ptr = temp;
							} else {
								TagNode tempSib = root.sibling;
								TagNode theWord = new TagNode(s, null, null);
								root.tag = tag;
								root.firstChild = theWord;
								root.sibling = tempSib;
								x = false;
								ptr = root;
							}
						} else {
							if (master.length() > 0) {
								TagNode tempSib = ptr.sibling;
								TagNode masterNode = new TagNode(master, null, null);
								ptr.sibling = masterNode;
								TagNode theWord = new TagNode(s, null, null);
								TagNode temp = new TagNode(tag, theWord, null);
								masterNode.sibling = temp;
								temp.sibling = tempSib;
								ptr = temp;
								master = " ";
								
								
							} else {
								TagNode tempSib = ptr.sibling;
								TagNode theWord = new TagNode(s, null, null);
								TagNode temp = new TagNode(tag, theWord, null);
								ptr.sibling = temp;
								temp.sibling = tempSib;
								ptr = temp;
								master = " ";
							}
						}
					} else if (s.toLowerCase().contains(word.toLowerCase()) && hasPunctuation(s, word) && s.length() == word.length()+1) {
						if (x == true) {
							if (master.length() > 0) {
								TagNode tempSib = root.sibling;
								TagNode theWord = new TagNode(s, null, null);
								TagNode temp = new TagNode(tag, theWord, null);
								root.tag = master;
								root.sibling = temp;
								temp.sibling = tempSib;
								master = " ";
								x = false;
								ptr = temp;
							} else {
								TagNode tempSib = root.sibling;
								TagNode theWord = new TagNode(s, null, null);
								root.tag = tag;
								root.firstChild = theWord;
								root.sibling = tempSib;
								x = false;
								ptr = root;
							}
						} else {
							if (master.length() > 0) {
								TagNode tempSib = ptr.sibling;
								TagNode masterNode = new TagNode(master, null, null);
								ptr.sibling = masterNode;
								TagNode theWord = new TagNode(s, null, null);
								TagNode temp = new TagNode(tag, theWord, null);
								masterNode.sibling = temp;
								temp.sibling = tempSib;
								ptr = temp;
								master = " ";
								
								
							} else {
								TagNode tempSib = ptr.sibling;
								TagNode theWord = new TagNode(s, null, null);
								TagNode temp = new TagNode(tag, theWord, null);
								ptr.sibling = temp;
								temp.sibling = tempSib;
								ptr = temp;
								master = " ";
							}			
						}
	
					} else {
						master += (s + " ");
						
					}

				}
				
				if (master != null && master != " " && ptr != null) {
					TagNode tempSib = ptr.sibling;
					TagNode masterNode = new TagNode(master,null,null);
					ptr.sibling = masterNode;
					masterNode.sibling = tempSib;
				}
				
			}
		}
		return root;
	}
	
	private boolean hasPunctuation(String s, String word) {
		if (s.charAt(s.length()-1) == '.' || s.charAt(s.length()-1) == ',' || s.charAt(s.length()-1) == '?' ||
				s.charAt(s.length()-1) == '!' || s.charAt(s.length()-1) == ':' || s.charAt(s.length()-1) == ';') {
			return true;
			
		}
		return false;
	}
	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
	/**
	 * Prints the DOM tree. 
	 *
	 */
	public void print() {
		print(root, 1);
	}
	
	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}
