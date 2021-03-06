/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ticktacktoe.controllers;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ticktacktoe.game.PlayFactory;
import ticktacktoe.game.Plays;
import ticktacktoe.game.singleplayer.SingleplayerWrapper;
import ticktacktoe.playersmatch.MatcherHelper;
import tictactoe.ai.AILevel;
import tictactoe.play.SingleplayerPlay;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
@Controller
public class SingleplayerMode {

    @RequestMapping("/menu/singleplayer/singleplayer.do")
    public String createSingleplayerPlay(HttpServletRequest rq, @RequestParam("level") int level) {
        String key = rq.getSession().getId();

        AILevel aiLevel = null;

        switch (level) {
            case 0:
                aiLevel = AILevel.Easy;
                break;
            case 1:
                aiLevel = AILevel.Medium;
                break;
            default:
                aiLevel = AILevel.Medium;
                break;
        }

        SingleplayerPlay play = PlayFactory.createDefaultSingleplayerPlay(aiLevel);
        SingleplayerWrapper wrap = new SingleplayerWrapper(play);
        wrap.register("You", key);
        Plays.getInstance().submitWrapper(wrap);
        return "redirect: " + rq.getContextPath() + "/play/" + play.getInfo().getId();
    }
}
