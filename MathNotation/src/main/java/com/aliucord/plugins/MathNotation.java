package com.github.Xolozop;

import android.content.Context;

import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PreHook;
import com.aliucord.utils.ReflectUtils;
import com.discord.widgets.chat.MessageContent;
import com.discord.widgets.chat.MessageManager;
import com.discord.widgets.chat.input.ChatInputViewModel;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kotlin.jvm.functions.Function1;

@SuppressWarnings("unused")
@AliucordPlugin
public class MathNotation extends Plugin {
    public static HashMap<String, String> constants = new HashMap<String, String>() {
        {
            put("pi", Character.toString('\u03c0'));
            put("alpha", Character.toString('\u03B1'));
            put("beta", Character.toString('\u03B2'));
            put("sigma", Character.toString('\u03C3'));
            put("gamma", Character.toString('\u03B3'));
            put("nu", Character.toString('\u03BD'));
            put("mu", Character.toString('\u03BC'));
            put("phi", Character.toString('\u03C6'));
            put("psi", Character.toString('\u03C8'));
            put("tau", Character.toString('\u03C4'));
            put("eta", Character.toString('\u03B7'));
            put("rho", Character.toString('\u03C1'));
        }
    };
    public static String[] greek = {"pi", "alpha", "beta", "sigma", "gamma", "nu", "mu", "phi", "psi", "tau", "eta", "rho"}; // constants

    public static HashMap<String, String> superscript = new HashMap<String, String>() {
		{
			put("0", Character.toString('\u2070'));
			put("1", Character.toString('\u00B9'));
			put("2", Character.toString('\u00B2'));
			put("3", Character.toString('\u00B3'));
			put("4", Character.toString('\u2074'));
			put("5", Character.toString('\u2075'));
			put("6", Character.toString('\u2076'));
			put("7", Character.toString('\u2077'));
			put("8", Character.toString('\u2078'));
			put("9", Character.toString('\u2079'));
			put("+", Character.toString('\u207A'));
			put("-", Character.toString('\u207B'));
			put("=", Character.toString('\u207C'));
			put("(", Character.toString('\u207D'));
			put(")", Character.toString('\u207E'));
			put("a", Character.toString('\u1D43'));
			put("b", Character.toString('\u1D47'));
			put("c", Character.toString('\u1D9C'));
			put("d", Character.toString('\u1D48'));
			put("e", Character.toString('\u1D49'));
			put("f", Character.toString('\u1DA0'));
			put("g", Character.toString('\u1D4D'));
			put("h", Character.toString('\u02B0'));
			put("i", Character.toString('\u2071'));
			put("j", Character.toString('\u02B2'));
			put("k", Character.toString('\u1D4F'));
			put("l", Character.toString('\u02E1'));
			put("m", Character.toString('\u1D50'));
			put("n", Character.toString('\u207F'));
			put("o", Character.toString('\u1D52'));
			put("p", Character.toString('\u1D56'));
			put("r", Character.toString('\u02B3'));
			put("s", Character.toString('\u02E2'));
			put("t", Character.toString('\u1D57'));
			put("u", Character.toString('\u1D58'));
			put("v", Character.toString('\u1D5B'));
			put("w", Character.toString('\u02B7'));
			put("x", Character.toString('\u02E3'));
			put("y", Character.toString('\u02B8'));
			put("z", Character.toString('\u1DBB'));
			put(" ", Character.toString(' '));
		}
	};
	public static HashMap<String, String> subscript = new HashMap<String, String>() {
		{
			put("0", Character.toString('\u2080'));
			put("1", Character.toString('\u2081'));
			put("2", Character.toString('\u2082'));
			put("3", Character.toString('\u2083'));
			put("4", Character.toString('\u2084'));
			put("5", Character.toString('\u2085'));
			put("6", Character.toString('\u2086'));
			put("7", Character.toString('\u2087'));
			put("8", Character.toString('\u2088'));
			put("9", Character.toString('\u2089'));
			put("+", Character.toString('\u208A'));
			put("-", Character.toString('\u208B'));
			put("=", Character.toString('\u208C'));
			put("(", Character.toString('\u208D'));
			put(")", Character.toString('\u208E'));
			put("a", Character.toString('\u2090'));
			put("e", Character.toString('\u2091'));
			put("h", Character.toString('\u2095'));
			put("i", Character.toString('\u1D62'));
			put("j", Character.toString('\u2C7C'));
			put("k", Character.toString('\u2096'));
			put("l", Character.toString('\u2097'));
			put("m", Character.toString('\u2098'));
			put("n", Character.toString('\u2099'));
			put("o", Character.toString('\u2092'));
			put("p", Character.toString('\u209A'));
			put("r", Character.toString('\u1D63'));
			put("s", Character.toString('\u209B'));
			put("t", Character.toString('\u209C'));
			put("u", Character.toString('\u1D64'));
			put("v", Character.toString('\u1D65'));
			put("x", Character.toString('\u2093'));
			put(" ", Character.toString(' '));
		}
	};
	public static HashMap<String, String> fractions = new HashMap<String, String>() {
		{
			put("1/2", Character.toString('\u00BD'));
			put("1/3", Character.toString('\u2153'));
			put("2/3", Character.toString('\u2154'));
			put("1/4", Character.toString('\u00BC'));
			put("3/4", Character.toString('\u00BE'));
			put("1/5", Character.toString('\u2155'));
			put("2/5", Character.toString('\u2156'));
			put("3/5", Character.toString('\u2157'));
			put("4/5", Character.toString('\u2158'));
			put("1/6", Character.toString('\u2159'));
			put("5/6", Character.toString('\u215A'));
			put("1/7", Character.toString('\u2150'));
			put("1/8", Character.toString('\u215B'));
			put("3/8", Character.toString('\u215C'));
			put("5/8", Character.toString('\u215D'));
			put("7/8", Character.toString('\u215E'));
			put("1/9", Character.toString('\u2151'));
			put("1/10", Character.toString('\u2152'));
		}
	};

    public static String help(String all) {
        String fraction = "a/n → \u1D43/\u2099" + '\n';
        String pow = "a^b, a**b, pow(a, b) → a\u1D47" + '\n';
        String sqrt = "sqrt(x) → \u221AX" + '\u0332' + '\n';
        //String sqrt = "sqrt(x) → \u221A" + 'x' + '\u203e' + '\n';
        String root = "rootn(x), root(n, x) → ⁿ\u221Ax" + "\u0332" + '\n';
        String abs = "abs(x) → |x|" + '\n';
        String substr = "x_n \u2192 x\u2099" + '\n';
        String exp = "exp(x) → e\u02E3" + '\n';
        //String lg = "lg(x) \u2192 log₁₀(x)" + '\n'; 
        String log = "log(x) \u2192 log₂(x)" + '\n';
        String loga = "loga(x), log(a, x) \u2192 log\u2090(x)" + '\n';
        all = '\n' + fraction + pow + sqrt + root + abs + substr + exp + log + loga + "\nConstants:\n";
        for (var i : greek) {
            all += i + " \u2192 " + constants.get(i) + ", ";
        }
		all = all.substring(0, all.length()-2);
		return all;
    } 

	public static String toSuperscript(String str) {
		String[] syms = str.split("");
		str = "";
		for (var i : syms) {
			if (superscript.get(i) != null) {
				str += superscript.get(i);
			} else {
				str += i;
			}
		}
		return str;
	}

	public static String toSubscript(String str) {
		String[] syms = str.split("");
		str = "";
		for (var i : syms) {
			if (subscript.get(i) != null) {
				str += subscript.get(i);
			} else {
				str += i;
			}
		}
		return str;
	}

	public static String toRoot(String power, String exp) {
		return toSuperscript(power) + "\u221A" + "__" + exp + "__ ";
	}

	public static String Convert(String str, String type) {
		if (type.equals("/") || type.equals("_")) {
			str = toSubscript(str.substring((type != "/") ? 1 : 0, str.length()));
		} else if (type.equals("^") || type.equals("exp")) {
			if (type == "exp") {
				str = toSuperscript(str.substring(str.indexOf("(") + 1, str.lastIndexOf(")"))) + str.substring(str.lastIndexOf(")") + 1, str.length());
			} else {
				str = toSuperscript(str.substring(type.length(), str.length()));
			}
		} else if (type.equals("abs")) {
			str = str.replace("abs(", "|");
			int lastInd = str.lastIndexOf(")");
			str = str.substring(0, lastInd) + "|" + str.substring(lastInd + 1, str.length());
		} else if (type.equals("sqrt") || type.equals("root")) {
			Matcher matcher = Pattern.compile("root\\(.+,\\s?.+\\)").matcher(str);
			if (matcher.find()) { 
				int space = (str.charAt(str.indexOf(",") + 1) == ' ') ? 2 : 1;
				str = toRoot(str.substring(str.indexOf("("), str.indexOf(",")), str.substring(str.indexOf(",") + space, str.length() - 2));
			} else {
				str = str.replace("sqrt(", "root(");
				str = toRoot(str.substring(4, str.indexOf("(")), str.substring(str.indexOf("(") + 1, str.length() - 2));
			}
		} else if (type.equals("log")) {
			String newstr = "";
			Matcher matcher = Pattern.compile("log\\(.+,\\s?.+\\)").matcher(str);
			if (matcher.find()) { 
				int space = (str.charAt(str.indexOf(",") + 1) == ' ') ? 2 : 1;
				newstr = "log" + toSubscript(str.substring(str.indexOf("(") + 1, str.indexOf(","))) + "(" + str.substring(str.indexOf(",") + space, str.length());
			} else {
				newstr = "log" + toSubscript((str.substring(3, str.indexOf("(")).isEmpty()) ? "2" : str.substring(3, str.indexOf("("))) + str.substring(str.indexOf("("), str.length());
			}
			str = newstr;
		} else if (type.equals("pow")) {
			String newstr = "";
			Matcher matcher = Pattern.compile("pow\\(.+,\\s?.+\\)").matcher(str);
			if (matcher.find()) { 
				int space = (str.charAt(str.indexOf(",") + 1) == ' ') ? 2 : 1;
				newstr = str.substring(str.indexOf("(") + 1, str.indexOf(",")) + toSuperscript(str.substring(str.indexOf(",") + space, str.length()-1));
			}
			str = newstr;
		} else {
			return str;
		}
		return str;
	}

	public static String reverseString(String str) {  
		StringBuilder sb = new StringBuilder(str);  
		sb.reverse();  
		return sb.toString();  
	} 


    @Override
    public void start(Context context) throws NoSuchMethodException {
        patcher.patch(ChatInputViewModel.class.getDeclaredMethod("sendMessage", Context.class, MessageManager.class, MessageContent.class, List.class, boolean.class, Function1.class),
                new PreHook(cf -> {
                    var thisobj = (ChatInputViewModel) cf.thisObject;
                    var content = (MessageContent) cf.args[2];
                    try {
                        var mes = content.component1().trim() + " "; // получить сообщение как строку
                        String newmes = "";
                        int ind = mes.indexOf("++[");
		        		newmes = mes.substring(0, ind);

						while (ind > -1 && mes.indexOf("]", ind) > -1) {
							String exp = mes.substring(ind+3, mes.indexOf("]", ind)); // expression without ++[]
	
							if (exp.equals("help")) {
								newmes += "\n```" + help("") + "```";
							} else {
								for (var c : greek) { exp = exp.replaceAll(c, constants.get(c));  } // constants
								exp = exp.replaceAll("\\*\\*", "\\^");
	
								for (String key: fractions.keySet()) {
									exp = exp.replace(key, fractions.get(key)); // fractions
								}
	
								int i = exp.indexOf("/");
								while (i > -1) {
									int p = i;
									//if (type == 1) p += operations[type][op].length();
									int s = 0;
									String operation = ""+exp.charAt(p);
									char symbol = 'a';
									while ((symbol != ' ' || s < 0) && p > 0) {
										p--;
										symbol = exp.charAt(p);
										if (symbol == '(') s++;
										if (symbol == ')') s--;
										if (s > 0) break;
										operation += symbol;
									}
									operation = StringFormatter.reverseString(operation);
									//System.out.println("|" + operation + "|" + toSuperscript(operation.substring(0, operation.length()-1))+ "/");
									exp = exp.replace(operation, toSuperscript(operation.substring(0, operation.length()-1)) + "/");
									i = exp.indexOf("/", i+1);
								}
								
								String[] operations = {"/", "^", "_", "exp", "abs", "sqrt", "pow", "root", "log"};
								for (var type = 0; type < operations.length; type++) {
									int p = exp.indexOf(operations[type]);
									while (p > -1) {
										int s = 0;
										String operation = ""+exp.charAt(p);
										char symbol = 'a';
										while ((symbol != ' ' || s > 0) && p < exp.length()-1) {
											p++;
											symbol = exp.charAt(p);
											if (symbol == '(') s++;
											if (symbol == ')') s--;
											if (s < 0) break;
											if (s == 0 && symbol == ',') break;
											operation += symbol;
										}
										exp = exp.replace(operation, ((operations[type] == "exp") ? "e" : "") + Convert(operation, operations[type]));
										p = (operations[type] == "/" || operations[type] == "log") ? exp.indexOf(operations[type], p + ((operations[type] == "log") ? 3 : 0)) : exp.indexOf(operations[type]);
									}
								}
								newmes += exp;
							}

						newmes += mes.substring(mes.indexOf("]", ind)+1, mes.indexOf("++[", mes.indexOf("]", ind)) == -1 ? mes.length() : mes.indexOf("++[", mes.indexOf("]", ind)));
						ind = mes.indexOf("++[", mes.indexOf("]", ind));
					}
					ReflectUtils.setField(content, "textContent", newmes.trim());
				} catch (NoSuchFieldException | IllegalAccessException e) {
					e.printStackTrace();
				}
		}));
    }

    @Override
    public void stop(Context context) {
        patcher.unpatchAll();
        commands.unregisterAll();
    }
}
