import React, { Component } from 'react';
import $ from 'jquery';
import Api from '../api';
import { NavLink, Link } from 'react-router-dom';
import Countdown from './countdown';


class UpdateCard extends Component {
    constructor() {
        super();
        this.state = {'update_date': new Date()}; 
    }

    timeSince() {
        var seconds = Math.floor((new Date() - this.state.update_date) / 1000);

        var interval = Math.floor(seconds / 86400);
        if (interval > 1) return "Updated " + interval + " days ago.";
        interval = Math.floor(seconds / 3600);
        if (interval > 1) return "Updated " + interval + " hours ago.";
        interval = Math.floor(seconds / 60);
        if (interval > 1) return "Updated " + interval + " minutes ago.";
        //if (seconds <= 15) return "Just updated." 
        return "Updated " + Math.floor(seconds) + " seconds ago.";
    }
}

class PerfCard extends UpdateCard {
    componentDidMount() {
        $().ready(function() {
            Api.getTeamMuHistory(function(perf) {
                var dataSales = {'series':[perf,perf], 'labels':[]};
                for (var i=perf.length-1; i>=0; i--)
                    dataSales.labels.push(i===0 ? "Now" : i + "hr ago");

                window.Chartist.Line('#mu_chart', dataSales, {
                    low: 0,
                    height: "245px",
                    axisX: { showGrid: false, },
                    lineSmooth: window.Chartist.Interpolation.simple({
                        divisor: 3
                    }), showLine: true,
                    showPoint: false,
                }, [['screen and (max-width: 640px)', {
                    axisX: {
                        labelInterpolationFnc: v => v[0]
                    }
                }]]);
            });
        });
    }

    render() {
        return (
            <div className="card">
                <div className="header">
                    <h4 className="title">Performance</h4>
                    <p className="category">Skill estimation over time.</p>
                </div>
                <div className="content">
                    <div id="mu_chart" className="ct-chart" />
                    <div className="footer">
                        <hr />
                        <div className="stats">
                            <i className="fa fa-history" /> { this.timeSince() }
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

class StatCard extends UpdateCard {
    componentDidMount() {
        $().ready(function() {
            Api.getTeamWinStats(function(stats) {
                window.Chartist.Pie('#stat_chart', {
                    labels: stats,
                    series: stats
                }); 
            });
        });
    }

    render() {
        return (
            <div className="card">
                <div className="header">
                    <h4 className="title">Match Statistics</h4>
                    <p className="category">Wins and losses.</p>
                </div>
                <div className="content">
                    <div id="stat_chart" className="ct-chart ct-perfect-fourth" />
                    <div className="footer">
                        <div className="legend">
                            <i className="fa fa-circle text-info" /> Win
                            <span style={{marginLeft: "10px"}}> </span>
                            <i className="fa fa-circle text-danger" /> Loss
                        </div>
                        <hr />
                        <div className="stats">
                            <i className="fa fa-clock-o" /> { this.timeSince() }
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

class DateCard extends UpdateCard {
    constructor() {
        super();
        this.state.dates = [];
    }

    componentDidMount() {
        Api.getUpdates(function(dates) {
            this.setState({ dates: (dates.length > 5)?dates.slice(0,5):dates  });
        }.bind(this));
    }

    render() {
        return (
            <div className="card ">
                <div className="header">
                    <h4 className="title">Recent Updates</h4>
                    <p className="category">A full listing can be found in the sidebar.</p>
                </div>
                <div className="content">
                    <div className="table-full-width">
                        <table className="table">
                            <tbody>
                                { this.state.dates.map(date => <tr key={ date.id }>
                                <td>{ date.time }</td>
                                <td>{ date.message }</td>
                                </tr> )}
                            </tbody>
                        </table>
                    </div>
                    <div className="footer">
                        <hr />
                        <div className="stats">
                            <i className="fa fa-history" /> { this.timeSince() }
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

class InstrCard extends UpdateCard {
    constructor() {
        super();
        this.state.dates = [];
    }

    componentDidMount() {
        // meh
    }

    render() {
        return (
            <div className="card ">
                <div className="header">
                    <h4 className="title">Welcome to Battlecode 2020!</h4>
                </div>
                <div className="content">
                    <p>
                        🍜
                    </p>
                </div>
            </div>
        );
    }
}


class LinksCard extends UpdateCard {
    constructor() {
        super();
    }

    componentDidMount() {
        // meh
    }
      

    render() {
        return (
            <div className="card ">
                <div className="header">
                    <h4 className="title">Useful Links</h4>
                </div>
                <div className="content">
                    <p>
                        <ul>
                            <li>
                                Discord
                            </li>
                            <li>
                                <a href='https://github.com/battlecode/battlecode20'>GitHub</a>
                            </li>
                            <li>
                                <a href='https://twitch.tv/mitbattlecode'>Twitch</a>
                            </li>
                            <li>
                                <a href='https://battlecode.org'>Battlecode.org</a>
                            </li>
                        </ul>
                    </p>
                </div>
            </div>
        );
    }
}

class Home extends Component {
    constructor() {
        super();
        this.state = {on_team:null};
    }

    componentDidMount() {
        Api.getUserTeam(function(e) {
            this.setState({on_team:(e !== null)});
        }.bind(this));
    }

    render() {
        return (
            <div className="content">
                <div className="container-fluid">
                    <div className="row">
                        <div className="col-md-6">
                            <div className="container-fluid">
                                <div className="row">
                                        <InstrCard />
                                </div>
                                <div className="row">
                                        <Countdown />
                                </div>
                                <div className="row">
                                        {this.state.on_team && <StatCard />}
                                </div>
                            </div>
                        </div>
                        <div className="col-md-6">
                            <div className="container-fluid">
                                <div className="row">
                                        <DateCard />
                                </div>
                                <div className="row">
                                        <LinksCard />
                                </div>
                                <div className="row">
                                        {this.state.on_team && <PerfCard />}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

export default Home;
