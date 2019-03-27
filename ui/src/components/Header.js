import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import './Header.css';

class Header extends PureComponent {
    constructor(props) {
        super(props);
        this.state = {
            loading: true,
            videos: [],
        };
    }

    render() {
        return (
            <div className='header-container'>
                <div className='logout-button' onClick={this.props.logout}>Logout</div>
            </div>
        );
    }

    static propTypes = {
        logout: PropTypes.func.isRequired,
    };

    static defaultProps = {
    };

}

export default Header;
