import React, { Component } from 'react';
import Api from '../api';
import './bracket.css';

class Bracket extends Component {

    state = {
        currentTournament: 0,
        gameIndex: 0,
        bracket: {
            "tournament": "",
            "rounds": [],
        },
    }

    componentDidMount() {
        Api.getTournamentBracket(this.state.currentTournament, (bracket) => {
            this.state.bracket = bracket;
        });
    }

    renderTeam(player, score, playernum, win) {

        const seedDisplay = player.seed != null ? player.seed : "?";
        const scoreElement = function(score) {
            if (score != null) {
                return (
                    <div className={"player-score player-round text-center " + (win ? "bg-primary" : "")} id={"score-"+this.state.gameIndex+"-"+playernum}>
                        {score}
                    </div>
                );
            }
            return "";
        }(score);
        const nameDisplay = player.seed != null ? player.name : (
            <small><em>
                {player.name}
            </em></small>
        );

        return (
            <div className={"list-group-item hover-trigger player player-"+playernum}>
                <div className="player-seed text-center">
                    <small id={"seed-"+this.state.gameIndex+"-"+playernum}>
                        {seedDisplay}
                    </small>
                </div>
                {scoreElement}
                <div className="player-name hover-target player-round" id={"name-"+this.state.gameIndex+"-"+playernum}>
                    {nameDisplay}
                </div>
                <div className="clearfix"></div>
            </div>
        );
    }

    renderMatch(player1, player2, score1, score2) {

        const team1 = this.renderTeam(player1, score1, 1, score1 > score2);
        const team2 = this.renderTeam(player2, score2, 2, score1 < score2);
        this.state.gameIndex++;

        return (
            <div className="col-xs-12 col-lg-6">
                <div className="row">
                    <div className="col-xs-3 text-right">
                        Match {this.state.gameIndex}
                    </div>
                    <div className="col-xs-9">
                        <div className="match-detail list-group">
                            {team1}
                            {team2}
                        </div>
                    </div>
                </div>
            </div>
        );
    }

    renderRound(roundName, games) {

        const results = [];
        for (const game in games) {
            results.push(this.renderMatch(
                game.red_team,
                game.blue_team,
                game.winner_ids.length > 0
                    ? game.winner_ids.reduce((a, b) => a + (b == game.red_team.id), 0)
                    : null,
                game.winner_ids.length > 0
                    ? game.winner_ids.reduce((a, b) => a + (b == game.blue_team.id), 0)
                    : null
            ));
        }

        return (
            <div className="row">
                <div className="col-xs-12 lead text-center">
                    {roundName}
                </div>
                {results}
            </div>
        );
    }

    render() {

        this.state.gameIndex = 0;

        const rounds = this.state.data.rounds.map(function(round) {
            return this.renderRound(round.round, round.games);
        }, this);

        return (
            <div className="content">
                <div className="container-fluid">
                    {rounds}
                </div>
            </div>
        );
    }
}

export default Bracket;
