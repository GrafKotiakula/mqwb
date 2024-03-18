package knu.csc.ttp.qualificationwork.mqwb.entities.game.jpa;

import knu.csc.ttp.qualificationwork.mqwb.abstractions.AbstractService;
import knu.csc.ttp.qualificationwork.mqwb.entities.company.Company;
import knu.csc.ttp.qualificationwork.mqwb.entities.company.jpa.CompanyService;
import knu.csc.ttp.qualificationwork.mqwb.entities.game.Game;
import knu.csc.ttp.qualificationwork.mqwb.entities.rating.Rating;
import knu.csc.ttp.qualificationwork.mqwb.entities.rating.jpa.RatingService;
import org.apache.logging.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class GameService extends AbstractService<Game, GameRepository> {
    protected CompanyService companyService;
    protected RatingService ratingService;

    @Autowired
    public GameService(GameRepository repository, CompanyService companyService, RatingService ratingService) {
        super(repository);
        this.companyService = companyService;
        this.ratingService = ratingService;
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

    private void updateCompanyAmounts(Set<UUID> updatedCompanyIds, Company company, String companyDesc) {
        if(company == null) {
            logger.trace("{} is null", companyDesc);
        } else if ( updatedCompanyIds.contains(company.getId()) ) {
            logger.trace("{} {} amounts are already updated", companyDesc, company);
        } else {
            company.setDevelopedGamesAmount(repository.countByDeveloper(company));
            company.setPublishedGamesAmount(repository.countByPublisher(company));

            companyService.update(company, null); // will not print logs
            logger.trace("{} {} amounts updated", companyDesc, company);

            updatedCompanyIds.add(company.getId());
        }
    }

    private void updateCompaniesAmounts(Game game, Company prevDev, Company prevPub){
        Set<UUID> updatedCompanyIds = new HashSet<>();
        updateCompanyAmounts(updatedCompanyIds, game.getDeveloper(), "Developer");
        updateCompanyAmounts(updatedCompanyIds, game.getPublisher(), "Publisher");
        updateCompanyAmounts(updatedCompanyIds, prevDev, "Previous developer");
        updateCompanyAmounts(updatedCompanyIds, prevPub, "Previous publisher");
    }

    @Override
    public Game create(Game game, Level logLevel) {
        game.setAvgRating( ratingService.create(new Rating(), Level.TRACE) );
        game = super.create(game, logLevel);
        updateCompaniesAmounts(game, null, null);
        return game;
    }

    @Override
    public Game update(Game game, Level logLevel) {
        Optional<Game> previousGame = findById(game.getId());
        game = super.update(game, logLevel);

        updateCompaniesAmounts( game,
                previousGame.map(Game::getDeveloper).orElse(null),
                previousGame.map(Game::getPublisher).orElse(null) );

        return game;
    }

    @Override
    public void delete(Game game) {
        Company developer = game.getDeveloper();
        Company publisher = game.getPublisher();
        game.setDeveloper(null);
        game.setPublisher(null);

        super.delete(game);

        updateCompaniesAmounts(game, developer, publisher);
        ratingService.delete(game.getAvgRating(), Level.TRACE);
    }
}
