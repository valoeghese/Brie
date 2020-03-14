package tk.valoeghese.brie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tk.valoeghese.brie.Struct.StructEntry;

/**
 * Half baked basil compiler. Doesn't actually work for all of what basil should do because it was just yeeted together as an example.
 */
final class Basil {
	private static final Struct VEC3;

	static byte[] compile(String source, Map<String, Struct> structures, Struct global) throws BasilSyntaxException {
		char[] chARR = source.toCharArray();
		ByteList result = new ByteList();

		StringBuilder buffer = new StringBuilder();

		boolean comment = false;
		int mode = MAIN;

		String varType = "";
		String cache = "";

		structures.put("global", global);
		structures.put("vec3", VEC3);

		Map<String, BasilVar> variables = new HashMap<>();

		List<String> varTypes = new ArrayList<>();
		varTypes.add("vec3");
		varTypes.add("float");
		varTypes.add("int");
		varTypes.add("global");

		byte nextId = 0;

		for (char c : chARR) {
			if (comment) {
				if (c == '\n') comment = false;
			} else {
				switch (c) {
				case '=':
					if (mode == ACT_ON_VARIABLE) {
						String str = buffer.toString();

						if (!str.isEmpty()) {
							throw new BasilSyntaxException("Unresolved symbol while compiling source!");
						}

						result.add(Opcodes.LOAD_VAR);
						result.add(variables.get(cache).id);

						mode = ASSIGN_VARIABLE;
						buffer = new StringBuilder();
					} else if (mode == SUB_VARIABLE) {
						String str = buffer.toString();

						if (cache.isEmpty()) {
							throw new BasilSyntaxException("Unexpected '.' while compiling source!");
						}

						// final part of var name
						cache += str;

						String[] components = str.split("\\.");

						// check this is a valid operation
						StringBuilder total = new StringBuilder();
						int finalType = -1;
						for (int i = 0; i < components.length; ++i) {
							final String component = components[i];

							if (i != 0) {
								// check type of variable
								BasilVar var = variables.get(total.toString());
								int type = var.type;

								if (type > 2 || type == 0) { // struct or vec3
									finalType = structures.get(varTypes.get(type)).getTypeForField(component);
								}
								total.append(".");
							}
							total.append(component);
						}

						if (finalType == -1) {
							throw new BasilSyntaxException("Recieved invalid type while resolving a struct/vec3/global reference!");
						}

						result.add(Opcodes.LOAD_VAR);
						result.add(variables.get(cache).id);

						varType = varTypes.get(finalType);
						mode = ASSIGN_VARIABLE;
						buffer = new StringBuilder();
					} else {
						throw new BasilSyntaxException("Unexpected '=' while compiling source!");
					}
					break;
				case '.':
					if (mode == MAIN) {
						String str = buffer.toString();

						// sub var
						cache = str + ".";
						mode = SUB_VARIABLE;
						buffer = new StringBuilder();
					} else if (mode == SUB_VARIABLE) { // even more sub
						String str = buffer.toString();

						cache = cache + str + ".";
						buffer = new StringBuilder();
					} else {
						throw new BasilSyntaxException("Unexpected '.' while compiling source!");
					}
					break;
				case ' ':
					String str = buffer.toString();

					if (str.isEmpty()) { // if it's empty it's just whitespace
						break;
					}

					if (mode == MAIN) {
						switch (str) {
						// check primitives: vec3, float, int
						case "vec3":
						case "float":
						case "int":
							varType = str;
							mode = NEW_VAR_NAME;
							break;
							// check structure definitions
						case "struct":
							throw new UnsupportedOperationException("structures not implemented yet!");
							// default checks
						default:
							if (str.trim().isEmpty()) {
								buffer = new StringBuilder();
							} else if (keywords.contains(str)) {
								throw new BasilSyntaxException("Unexpected keyword while compiling source!");
							} else if (structures.containsKey(str)) {
								throw new UnsupportedOperationException("structures not implemented yet!");
							} else if (variables.containsKey(str)) {
								mode = ACT_ON_VARIABLE;
								cache = str;
								buffer = new StringBuilder();
							} else {
								throw new BasilSyntaxException("Unknown symbol found while compiling source!");
							}
							break;
						}
						// don't append
						break;
					} else if (mode == NEW_VAR_NAME) {
						String varName = buffer.toString().trim(); // just in case

						if (variables.containsKey(varName)) {
							throw new BasilSyntaxException("Variable " + varName + " already defined!");
						} else if (keywords.contains(varName)) {
							throw new BasilSyntaxException("Cannot use reserved keyword " + varName + " as a variable name!");
						}

						// add variable
						BasilVar variable = new BasilVar();
						variable.type = varTypes.indexOf(varType);
						variable.id = nextId++;
						variables.put(varName, variable);

						// add variable, in bytecode
						result.add(Opcodes.newVar(variable.type));
						buffer = new StringBuilder();
						break;
					} else {
						throw new BasilSyntaxException("Unexpected space while compiling source!");
					}
				case '/':
					if (buffer.toString().endsWith("/")) {
						buffer = new StringBuilder();
						comment = true;
					} else {
						buffer.append(c);
					}
					break;
				default:
					if (!Character.isWhitespace(c)) buffer.append(c);
					break;
				}
			}
		}

		return result.toArray();
	}

	static final ArrayList<String> keywords;

	static {
		keywords = new ArrayList<>();
		keywords.add("vec3");
		keywords.add("float");
		keywords.add("int");
		keywords.add("global");
		keywords.add("struct");

		VEC3 = new Struct();
		VEC3.entries.add(new StructEntry("x", Struct.FLOAT));
		VEC3.entries.add(new StructEntry("y", Struct.FLOAT));
		VEC3.entries.add(new StructEntry("z", Struct.FLOAT));
	}

	static final int
	MAIN = 0,
	NEW_VAR_NAME = 1,
	ACT_ON_VARIABLE = 2,
	ACT_ON_NEW = 3,
	SUB_VARIABLE = 4,
	ASSIGN_VARIABLE = 5;
}

class BasilVar {
	int type;
	byte id;
}

class BasilSyntaxException extends RuntimeException {
	public BasilSyntaxException(String e) {
		super(e);
	}

	private static final long serialVersionUID = -7927518915409731717L;
}

interface Opcodes {
	byte
	NEW_VEC3 = 0, // create new vec3 var [1]
	NEW_FLOAT = 1, // create new float var [1]
	NEW_INT = 2, // create new int var [1]
	NEW_STRUCT = 3, // create new struct var [1]
	LOAD_VAR = 4, // load the specified var [2]
	LOAD_INT_VAL = 5, // load the specified value [5]
	LOAD_FLOAT_VAL = 6, // load the specified value [5]
	STORE_VAR = 7; // store the 2nd value in the stack in the 1st value [1]

	static byte newVar(int type) {
		return (type < 0 || type > 2) ? NEW_STRUCT : (byte) type;
	}
}

class Struct {
	List<StructEntry> entries = new ArrayList<>();

	int getTypeForField(String name) {
		try {
			return this.entries.get(this.entries.indexOf(StructEntry.search(name))).type;
		} catch (IndexOutOfBoundsException e) {
			throw new BasilSyntaxException("Recieved invalid struct index while compiling!");
		}
	}

	static class StructEntry {
		String name;
		int type;

		StructEntry(String name, int type) {
			this.name = name;
			this.type = type;
		}

		private StructEntry() {
		}

		private static Object search(String name) {
			StructEntry result = new StructEntry();
			result.name = name;
			return result;
		}

		@Override
		public int hashCode() {
			return this.name.hashCode();
		}

		@Override
		public boolean equals(Object other) {
			if (other == this) {
				return true;
			} else if (other instanceof StructEntry) {
				return this.name.equals(((StructEntry) other).name);
			} else {
				return false;
			}
		}
	}

	static final int
	VEC3 = 0,
	FLOAT = 1,
	INT = 2;
}

class ByteList {
	ByteList() {
		this(16);
	}

	ByteList(int initialCapacity) {
		this.size = initialCapacity;
		this.array = new byte[initialCapacity];
	}

	private int size;
	private byte[] array;

	byte get(int index) throws IndexOutOfBoundsException {
		if (index < this.size) {
			return this.array[index];
		} else {
			throw new IndexOutOfBoundsException("Index" + index + " is greater than ByteArrayList size " + this.size + "!");
		}
	}

	void add(byte value) {
		if (this.size < this.array.length) {
			this.array[this.size++] = value;
		} else {
			this.array = this.arrayCopy(new byte[++this.size]);
			this.array[this.size - 1] = value;
		}
	}

	byte[] toArray() {
		return this.arrayCopy(new byte[this.size]);
	}

	private byte[] arrayCopy(byte[] arr) {
		System.arraycopy(this.array, 0, arr, 0, this.size);
		return arr;
	}
}