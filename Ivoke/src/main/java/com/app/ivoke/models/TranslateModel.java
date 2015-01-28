package com.app.ivoke.models;

import java.net.URLEncoder;

import com.app.ivoke.helpers.WebHelper.NetworkException;
import com.app.ivoke.helpers.WebHelper.ServerException;

public class TranslateModel extends WebServer {

    final String googleUrlTranslate =
     "http://translate.google.com/translate_a/t?client=t&text=%s&hl=%s&sl=%s&tl=%s&ie=UTF-8&oe=UTF-8&multires=1&otf=1&ssel=3&tsel=3&sc=1";


    public String translate(String pPhrase, Languages pFromLang, Languages pToLang) throws ServerException, NetworkException, Exception
    {
        pPhrase = pPhrase.replace("\n", "|");

        String fromLang       = getParameterForGoogle(pFromLang);
        String toLang         = getParameterForGoogle(pToLang);
        String formatedPhrase = URLEncoder.encode(pPhrase);
        String formatedURL       = String.format(googleUrlTranslate, formatedPhrase, fromLang, fromLang, toLang);

        String res = web.doPostRequest(formatedURL, null);

        String phraseTranslated = res.substring(res.indexOf("[[[\""), res.indexOf("\",\"")).substring(4);

        return phraseTranslated.replace("|", "\n");
    }

    private String getParameterForGoogle(Languages pLang)
    {
        switch (pLang) {
        case PT:
            return "pt";
        case EN:
            return "en";
        }
        return null;
    }

    public enum Languages
    {
        PT, EN
    }

}
