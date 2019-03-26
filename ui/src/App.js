import React, { Component } from 'react';
import Login from './Login';
import './App.css';

class App extends Component {
    constructor(props) {
        super(props);
        this.state = {
            jwt: null,
            loading: false,
            badLogin: false,
        };
        this.login = this.login.bind(this);
    }

    login(username, password) {
      fetch("http://localhost:8080/login", {
        method: "POST",
        mode: "cors",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          username,
          password,
        })
      }).then(res => {
        if(!res.ok) {
          this.setState({
            badLogin: true,
            loading: false,
          });
        } else {
          this.setState({
            loading: false,
            badLogin: false,
            jwt: res.headers.get("jwt-authorization"), 
          });
        }
      });
      this.setState({
        loading: true, 
      });
    }

    render() {
        const { jwt, loading, badLogin } = this.state; 
        return (
            <div className="App">
                {jwt == null ?
                <Login processingLogin={loading} login={this.login} badLogin={badLogin}/> : 
                <header className="App-header">
                    <p>
                        Dionysus Video Streamer
                    </p>
                </header>
                }
            </div>
        );
    }
}

export default App;
