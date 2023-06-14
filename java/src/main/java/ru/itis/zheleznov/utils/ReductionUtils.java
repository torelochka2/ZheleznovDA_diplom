package ru.itis.zheleznov.utils;

import java.util.HashMap;
import java.util.Map;

public class ReductionUtils {

    public static Map<String, String> reductions = new HashMap<>();

    static {
        reductions.put("авт\\.", "автор");
        reductions.put("авт\\. и сост\\.", "автор и составитель");
        reductions.put("автреф\\.", "автореферат");
        reductions.put("акад\\.", "академия");
            reductions.put("аналит\\.", "аналитический");
        reductions.put("англ\\.", "английский");
        reductions.put("аннот\\.", "аннотация");
        reductions.put("библиогр\\.", "библиография");
        reductions.put("бюл\\.", "бюллютень");
        reductions.put("газ\\.", "газета");
        reductions.put("гл\\.", "глава");
        reductions.put("гос\\.", "государственный");
        reductions.put("дис\\.", "диссертация");
        reductions.put("доп\\.", "дополненный");
        reductions.put("журн\\.", "журнал");
        reductions.put("\\[и др.\\]", "[и другие]");
        reductions.put("изд\\.", "издание");
        reductions.put("изд-во", "издательство");
        reductions.put("испр\\.", "исправленный");
        reductions.put("ин-т", "институт");
        reductions.put("информ\\.", "информационный");
        reductions.put("ист\\.", "исторический");
        reductions.put("канд\\.", "кандидат");
        reductions.put("конф\\.", "конференция");
        reductions.put("м-во", "министерство");
        reductions.put("моногр\\.", "монография");
        reductions.put("Москв\\.", "Московский");
        reductions.put("науч\\.", "научный");
        reductions.put("нем\\.", "немецкий");
        reductions.put("обл\\.", "областной");
        reductions.put("отв\\.", "ответственный");
        reductions.put("отв\\. ред\\.", "ответственный редактор");
        reductions.put("офиц\\. текст", "официальный текст");
        reductions.put("пер\\.", "перевод");
        reductions.put("перераб\\.", "переработанный");
        reductions.put("пер\\. с англ\\.", "перевод с английского");
        reductions.put("переизд\\.", "переиздание");
        reductions.put("под ред\\.", "под редакцией");
        reductions.put("под общ\\. ред\\.", "под общей редакцией");
        reductions.put("попул\\.", "популярный");
        reductions.put("пед\\.", "педагогических");
        reductions.put("посвящ\\.", "посвященный");
        reductions.put("прил\\.", "приложение");
        reductions.put("редкол\\.", "редакционная коллегия");
        reductions.put("рус\\.", "русский");
        reductions.put("сб\\.", "сборник");
        reductions.put("сред\\.", "средних");
        reductions.put("шк\\.", "школ");
        reductions.put("сер\\.", "серия");
        reductions.put("слов\\.", "словарь");
        reductions.put("собр\\.", "собрание");
        reductions.put("сост\\.", "составитель");
        reductions.put("справ\\.", "справочник");
        reductions.put("справ\\. пособие", "справочное пособие");
        reductions.put("темат\\.", "тематический");
        reductions.put("т\\.", "том");
        reductions.put("ун-т", "университет");
        reductions.put("ун-та", "университета");
        reductions.put("утв\\.", "утвержден");
        reductions.put("учеб\\. пособие", "учебное пособие");
        reductions.put("учеб\\.-метод\\. пособие", "учебно-методическое пособие");
        reductions.put("федер\\. закон", "федеральный закон");
        reductions.put("фр\\.", "французский");
        reductions.put("ч\\.", "часть");
        reductions.put("экон\\.", "экономических");
        reductions.put("энцикл\\.", "энциклопедия");
        reductions.put("яз\\.", "язык");
    }
}
