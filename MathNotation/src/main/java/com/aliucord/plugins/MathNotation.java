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
    public static String[] greek= {"pi", "alpha", "beta", "sigma", "gamma", "nu", "mu", "phi", "psi", "tau", "eta", "rho"}; // constants - type 1

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
    public static String Convert(String a, int type) {
        String new_a = "";
        for (String sym : a.split("")) {
            switch(type) {
                case 1: new_a += superscript.get(sym); break;
                case 2: new_a += subscript.get(sym); break;
            }
        }
        return new_a;
    }
    public static String Type2(String s) {
        if (fractions.get(s) == null) {
            String[] new_s = s.split("\\/|\\*\\*|\\^|_");
            if (s.contains("/")) {
                return Convert(new_s[0], 1) + "/" + Convert(new_s[1], 2);  // fractions and etc - type 2
            } else {
                if (s.contains("_")) {
                    return new_s[0] + Convert(new_s[1], 2);
                } else {
                    return new_s[0] + Convert(new_s[1], 1);
                }
            }
        } else {
            return fractions.get(s);
        }
    }

    public static String help(String all) {
        String fraction = "a/n → \u1D43/\u2099" + '\n';
        String pow = "a^b, a**b, pow(a, b) → a\u1D47" + '\n';
        String sqrt = "sqrt(x) → \u221AX" + '\u031A' + '\n';
        //String sqrt = "sqrt(x) → \u221A" + 'x' + '\u203e' + '\n';
        String root = "rootn(x), root(n, x) → ⁿ\u221Ax" + Character.toString('\u0304') + '\n';
        String abs = "abs(x) → |x|" + '\n';
        String substr = "x_n \u2192 x\u2099" + '\n';
        String exp = "exp(x) → e\u02E3" + '\n';
        //String lg = "lg(x) \u2192 log₁₀(x)" + '\n'; 
        String log = "log(x) \u2192 log₂(x)" + '\n';
        String loga = "loga(x), log(a, x) \u2192 log\u2090(x)" + '\n';
        all = '\n' + fraction + pow + sqrt + root + abs + substr + exp + log + loga + "\nConstants:\n"; // help
        for (var i : greek) {
            all += i + " \u2192 " + constants.get(i) + '\n';
        }
		return all;
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
		            String exp = mes.substring(ind+3, mes.indexOf("]", ind)); // x**6 + 13 - 12/13
		
		            if (exp.equals("help")) {
		                newmes += "\n```" + help("") + "```";
		            } else {
		                // fraction, pow, sqrt, abs, substr, exp, log, constants
		
		                for (var c : greek) {
		                    exp = exp.replaceAll(c, constants.get(c)); // constants
		                }
		                //exp = "12/4 ";
		                //String as1 = "((\\s[^\\s]*)|(^[^\\s]*)|(\\([^\\s]*))";
		                //String as2 = "(([^\\s]*\\s)|([^\\s]*$)|([^\\s]*\\)))";
		                String as1 = "[^\\s\\(\\)]*", as2 = as1; // any symbol, except space and ( )
		                Matcher matcher = Pattern.compile(as1+"\\/"+as2+ "|" +as1+"\\*\\*"+as2+ "|" +as1+"\\^"+as2+ "|" +as1+"_"+as2).matcher(exp);
		                List<String> matchesList = new ArrayList<String>();
		                while (matcher.find()) { 
		                    matchesList.add(matcher.group());
		                }
		                for (var find : matchesList) {
		                    //System.out.println("here: " + find + "!");
		                    exp = exp.replace(find, Type2(find)); // fractions, pow, nth element
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
