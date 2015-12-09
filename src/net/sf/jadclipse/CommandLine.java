package net.sf.jadclipse;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Represents a generic command line consisting of a command and options.
 * It is assumed that options with numeric values do not have a whitespace
 * after option name.
 * 
 * Work in progress...
 * 
 * @author V.G.
 */
public class CommandLine {
	private String command;
	private List options = new ArrayList();
	
	public CommandLine(String command, List options) {
		if (command == null || command.trim().length() == 0) {
			throw new IllegalArgumentException("bogus command");
		}
		this.command = command;
		if (options != null) {
			this.options.addAll(options);
		}
	}
	
	/**
	 * @return
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * @return
	 */
	public List getOptions() {
		return options;
	}
	
	public CommandOption getOption(String name) {
		if (name == null) {
			throw new IllegalArgumentException("null name");
		}
		for (Iterator iter = options.iterator(); iter.hasNext();) {
			CommandOption option = (CommandOption)iter.next();
			if (name.equals(option.getName())) {
				return option;
			}
		}
		return null;
	}
	
	public int getOptionCount() {
		return options.size();
	}
	
	public void addOption(CommandOption option) {
		options.add(option);
	}
	
	public void clearOptions() {
		options.clear();
	}
	
	private static int findTrailingNumber(String token) {
		int numIdx = -1;
		//check if this is a numeric option
		for (int i = (token.length() - 1); i >= 0; i--) {
			char ch = token.charAt(i); 
			if (ch >= '0' && ch <= '9') {
				numIdx = i;
			}
			else { //break on first non-digit from end
				break;
			}
		}
		return numIdx;
	}
	
	private static NumericOption createNumericOption(String token, int numIdx) {
		String optName = token.substring(0, numIdx);
		int numValue = Integer.parseInt(token.substring(numIdx));
		return new NumericOption(optName, numValue);
	}
	
	private static NumericOption tryNumericOption(String token) throws ParseException {
		int numIdx = findTrailingNumber(token);
		if (numIdx == 0 || numIdx == 1) {
			throw new ParseException("option cannot start with a number", numIdx);
		}
		return (numIdx > 0) ? createNumericOption(token, numIdx) : null;
	}
	
	public static CommandLine parse(String line, String optionPrefix) throws ParseException {
		if (line == null) {
			throw new IllegalArgumentException("null line");
		}
		if (optionPrefix == null) {
			throw new IllegalArgumentException("null option prefix");
		}
		int optStart = line.indexOf(optionPrefix);
		if (optStart == 0) {
			throw new ParseException("missing command", 0);
		}
		if (optStart < 0) { //no options found
			return new CommandLine(line.trim(), null);
		}
		String cmd = line.substring(0, optStart).trim();
		ArrayList opts = new ArrayList();
		StringTokenizer st = new StringTokenizer(line.substring(optStart), "\t ");
		String optName = null;
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			boolean option = token.startsWith(optionPrefix);
			if (option) { //found option start
				if (optName != null) { //create a no-value option
					opts.add(new CommandOption(optName, null));
					optName = null;
				}
				//check if this is a numeric option
				NumericOption numOpt = tryNumericOption(token);
				if (numOpt != null) {
					opts.add(numOpt);
				}
				else {
					optName = token;
				}
			}
			else { //found option value
				if (optName == null) {
					throw new ParseException("value without a name", 0);
				}
				opts.add(new CommandOption(optName, token));
				optName = null;
			}
		}
		if (optName != null) { //do not forget the last one!
			//check if this is a numeric option
			NumericOption numOpt = tryNumericOption(optName);
			if (numOpt != null) {
				opts.add(numOpt);
			}
			else { //create a non-numeric option
				opts.add(new CommandOption(optName, null));
			}
		}
		return new CommandLine(cmd, opts);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer(command).append(" ");
		for (Iterator iter = options.iterator(); iter.hasNext();) {
			CommandOption option = (CommandOption)iter.next();
			buf.append(option.toString()).append(" ");
		}
		buf.setLength(buf.length() - 1); //remove last white space
		return buf.toString();
	}
	
	public static void main(String[] args) throws Exception {
		CommandLine cl = CommandLine.parse("C:\\Program Files\\jad -zxc ttt -x -lff100 -bb", "-");
		System.out.println("command: " + cl.getCommand());
		System.out.println("option count: " + cl.getOptionCount());
		List options = cl.getOptions();
		for (int i = 0; i < options.size(); i++) {
			CommandOption opt = (CommandOption)options.get(i);
			System.out.println("option_" + i + "(" + opt.getClass().getName() +"): " + opt.toString());
		}
		System.out.println("-lff=" + cl.getOption("-lff").getValue());
		System.out.println(cl.toString());
	}

}
