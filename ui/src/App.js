import React, { Component } from 'react';
import Login from './Login';
import VideoContainer from './components/VideoContainer';
import Header from './components/Header';
import { POST, setJWT } from './helpers/API';
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
        this.logout = this.logout.bind(this);
    }

    componentDidMount(props) {
      const jwt = localStorage.getItem("jwt-authorization");
      if(jwt != null) {
        const token = this.parseJWTToken(jwt);
        //check if token exp is greater than now in UTC seconds
        if(token && token.exp > Date.now()/1000) {
          this.setState({
            jwt,
          });
          setJWT(jwt);
        }
      }
    }

    login(username, password) {
      POST({path: "login", body: {
        username,
        password,
      }}).then(res => {
        if(!res.ok) {
          this.setState({
            badLogin: true,
            loading: false,
          });
        } else {
          const jwt = res.headers.get("jwt-authorization");
          setJWT(jwt);
          localStorage.setItem("jwt-authorization", jwt);
          this.setState({
            loading: false,
            badLogin: false,
            jwt: jwt, 
          });
        }
      });
      this.setState({
        loading: true, 
      });
    }

    logout() {
      setJWT(null);
      localStorage.removeItem("jwt-authorization");
      this.setState({
        loading: false,
        badLogin: false,
        jwt: null, 
      });
    }

    parseJWTToken(token) {
      var base64Url = token.split('.')[1];
            var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
            return JSON.parse(window.atob(base64));
    }

    render() {
        const { jwt, loading, badLogin } = this.state; 
        return (
            <div className="App">
                {jwt == null ?
                <Login processingLogin={loading} login={this.login} badLogin={badLogin}/> : 
                <div className="App-Container">
                  <Header logout={this.logout}/>
                  <VideoContainer jwt={jwt}/>
                </div>
                }
            </div>
        );
    }
}

export default App;
