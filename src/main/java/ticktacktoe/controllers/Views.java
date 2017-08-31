/*
 * The MIT License
 *
 * Copyright 2017 Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ticktacktoe.controllers;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static javax.swing.text.StyleConstants.Size;
import javax.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ticktacktoe.game.Plays;
import ticktacktoe.game.multiplayer.MultiplayerWrapper;
import ticktacktoe.exceptions.GameNotFoundException;
import ticktacktoe.exceptions.UnprivilegedAccessException;
import ticktacktoe.game.PlayParticipant;
import ticktacktoe.game.Wrapper;
import ticktacktoe.game.singleplayer.SingleplayerWrapper;
import ticktacktoe.playersmatch.MatcherHelper;
import pl.michal.szymanski.tictactoe.play.Play;

/**
 *
 * @author Michał Szymański, kontakt: michal.szymanski.aajar@gmail.com
 */
@Controller
public class Views {

    @RequestMapping("/create/singleplayer")
    public String getCreatePlay(Model model) {
        model.addAttribute("page", "create/create");
        model.addAttribute("create_mode", "create/singleplayer");
        return "template";
    }

    @RequestMapping("/")
    public String getMenu() {
        return "redirect: menu";
    }

    @RequestMapping("/play/{id}")
    public String getPlay(@PathVariable("id") String gameid, Model model, HttpServletRequest rq, HttpServletResponse rs) {
        Optional<Wrapper> play = Plays.getInstance().getPlay(gameid);
        String key = rq.getSession().getId();

        if (!play.isPresent()) {
            throw new GameNotFoundException();
        }

        if (!canAccessGame(play.get(), key)) {
            throw new UnprivilegedAccessException();
        }

        String title = null;
        if (play.get() instanceof MultiplayerWrapper) {
            title = generateMutliplayerPlayTitle((MultiplayerWrapper) play.get(), key);
        }
        model.addAttribute("game_title", title);
        model.addAttribute("gameid", play.get().getPlay().getInfo().getId());
        model.addAttribute("game_time", play.get().getPlay().getSettings().getters().getGameTimeLimitInMilis());
        model.addAttribute("turn_time", play.get().getPlay().getSettings().getters().getTurnTimeLimitInMilis());
        model.addAttribute("player_key", key);
        model.addAttribute("page_name", "Play");
        return "arena";
    }

    public boolean canAccessGame(Wrapper wrap, String key) {
        if (wrap instanceof MultiplayerWrapper) {
            MultiplayerWrapper wrapper = (MultiplayerWrapper) wrap;
            PlayParticipant p1 = wrapper.getPlayers().getFirst();
            PlayParticipant p2 = wrapper.getPlayers().getSecond();
            return (key.equals(p1.getId()) || key.equals(p2.getId()));

        } else if (wrap instanceof SingleplayerWrapper) {
            SingleplayerWrapper wrapper = (SingleplayerWrapper) wrap;
            return wrapper.getPlayer().getId().equals(key);
        }

        return true;
    }

    public String generateMutliplayerPlayTitle(MultiplayerWrapper wrap, String key) {
        PlayParticipant p1 = wrap.getPlayers().getFirst();
        PlayParticipant p2 = wrap.getPlayers().getSecond();
        String title = "You vs {0}";
        String oponentName = p1.getId().equals(key) ? p1.getUsername() : p2.getUsername();
        title = MessageFormat.format(title, oponentName);
        return title;
    }

    @RequestMapping("/menu")
    public String getMenu(Model model) {
        model.addAttribute("page", "menu");
        model.addAttribute("page_name", "Welcome in Tic Tac Toe Game!");
        return "template";
    }

    @RequestMapping("/menu/singleplayer/join")
    public String getCreateSingleplayer(Model model) {
        model.addAttribute("page", "singleplayer");
        model.addAttribute("page_name", "Play against computer");
        return "template";
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity handleBadRequest(RuntimeException e) {
        return ResponseEntity.badRequest().build();
    }

    public String randomUsername() {
        return "anonymous@" + System.currentTimeMillis();
    }

    @RequestMapping(value = "/menu/multiplayer/join/play_searcher", method = RequestMethod.GET)
    public String getPlaySearcher(Model model, @CookieValue(value = "username", defaultValue = "") String username,
            HttpServletResponse rs, HttpServletRequest rq) {

        String key = rq.getSession().getId();

        if (username.isEmpty()) {
            username = "anonymous@" + Integer.toHexString((int) System.nanoTime());
        }

        model.addAttribute("page", "play_searcher");
        model.addAttribute("player_username", username);
        model.addAttribute("player_key", key);
        model.addAttribute("page_name", "Searching for opponent...");
        model.addAttribute("status", "Searching for opponent...");

        return "template";
    }

}
