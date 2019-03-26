import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';

import LoadingModal from './components/modals/LoadingModal';
import './Login.css';

class Login extends PureComponent {
    constructor(props) {
        super(props);
        this.state = {
            username: "",
            password: "",
        };
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleChangeUsername = this.handleChangeUsername.bind(this);
        this.handleChangePassword = this.handleChangePassword.bind(this);
    }

    handleSubmit = event => {
        event.preventDefault();
        const { login } = this.props;
        const { username, password } = this.state;
        login(username, password);
    }

    handleChangeUsername(event) {
        this.setState({username: event.target.value});
    }
    

    handleChangePassword(event) {
        this.setState({password: event.target.value});
    }

    render() {
        const { badLogin, processingLogin } = this.props;
        return (
            <form onSubmit={this.handleSubmit}>
                <div className="login-container">
                    <div className="username-container">
                        <div>Username:</div>
                        <input type="text" onChange={this.handleChangeUsername}/>
                    </div>
                    <div className="password-container">
                        <div>Password:</div>
                        <input type="password" onChange={this.handleChangePassword}/>
                    </div>
                    <button type="submit" disabled={processingLogin}>Login</button>
                    {badLogin && <div className="error-text">Invalid username or password</div>}
                    <LoadingModal show={processingLogin}/>
                </div>
            </form>
        );
    }

    static propTypes = {
        login: PropTypes.func.isRequired,
        processingLogin: PropTypes.bool,
        badLogin: PropTypes.bool,
    };

    static defaultProps = {
        processingLogin: false,
        badLogin: false,
    };

}

export default Login;
