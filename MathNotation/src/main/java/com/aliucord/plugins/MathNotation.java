package com.github.Xolozop;

import android.content.Context;

import com.aliucord.annotations.AliucordPlugin;
import com.aliucord.entities.Plugin;
import com.aliucord.patcher.PreHook;
import com.aliucord.utils.ReflectUtils;
import com.discord.widgets.chat.MessageContent;
import com.discord.widgets.chat.MessageManager;
import com.discord.widgets.chat.input.ChatInputViewModel;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import kotlin.jvm.functions.Function1;

@SuppressWarnings("unused")
@AliucordPlugin
public class MathNotation extends Plugin {
    public static String help(String all) {
        String fraction = "a/n → \u1D43/\u2099" + '\n';
        String pow = "a^b, a**b, pow(a, b) → a\u1D47" + '\n';
        String sqrt = "sqrt(x) → \u221Ax" + Character.toString('\u0304') + '\n';
        String root = "rootn(x), root(n, x) → ⁿ\u221Ax" + Character.toString('\u0304') + '\n';
        String abs = "abs(x) → |x|" + '\n';
        String substr = "x_n \u2192 x\u2099" + '\n';
        String exp = "exp(x) → e\u02E3" + '\n';
        //String lg = "lg(x) \u2192 log₁₀(x)" + '\n'; 
        String log = "log(x) \u2192 log₂(x)" + '\n';
        String loga = "loga(x), log(a, x) \u2192 log\u2090(x)" + '\n';
        all = sqrt + root + abs + exp + fraction + pow + log + loga + substr + "\nConstants:";
        String[] greek= {"pi", "alpha", "beta", "sigma", "gamma", "nu", "mu", "phi", "psi", "tau", "eta", "rho"};
        String[] constants= {"\u03c0", "\u03B1", "\u03B2", "\u03C3", "\u03B3", "\u03BD", "\u03BC", "\u03C6", "\u03C8", "\u03C4", "\u03B7", "\u03C1"};
        for (var i = 0; i < greek.length; i++) {
            all += greek[i] + " \u2192 " + constants[i] + '\n';
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

                        while (ind > -1 && mes.indexOf("]", ind) > -1) {
                            String exp = mes.substring(ind+3, mes.indexOf("]", ind)); // x**6 + 13 - 12/13

                            if (exp.equals("help")) {
                                newmes += help("");
                            } else {
                                newmes += exp;
                            }
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
