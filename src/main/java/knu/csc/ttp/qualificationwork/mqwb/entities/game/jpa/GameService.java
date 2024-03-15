package knu.csc.ttp.qualificationwork.mqwb.entities.game.jpa;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractService;
import knu.csc.ttp.qualificationwork.mqwb.entities.game.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GameService extends AbstractService<Game, GameRepository> {
    @Autowired
    public GameService(GameRepository repository) {
        super(repository);
        defaultSort = Sort.by(Sort.Direction.DESC, "release");
    }

    public Page<Game> findAllByNameContains(String name, int page) {
        Pageable pageable = pageableOf(page);
        return Optional.ofNullable(name)
                .map(n -> repository.findAllByNameContains(n, pageable) )
                .orElse(Page.empty(pageable));
    }

    public Optional<Game> findByName(String name) {
        return Optional.ofNullable(name).flatMap(repository::findByName);
    }
}
