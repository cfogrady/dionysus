import React, { PureComponent } from 'react';
//import PropTypes from 'prop-types';
import { GET } from '../helpers/API';
import LoadingModal from './modals/LoadingModal';
import './VideoContainer.css';

class VideoContainer extends PureComponent {
    constructor(props) {
        super(props);
        this.state = {
            loading: true,
            videos: [],
        };
        this.fetchVideos = this.fetchVideos.bind(this);
    }

    componentDidMount(props) {
        this.fetchVideos(null);
    }

    fetchVideos(group) {
        const path = group == null ? 'video/group' : `video/group/${group}`;
        GET({
            path,
        }).then(res => {
            if(res.ok) {
                return res.json();
            } else {
                console.error('Failed to load videos', res);
            }
        }).then(data => {
            this.setState({
                loading: false,
                videos: data,
            });
        });
        this.setState({
            loading: true,
        });
    }

    render() {
        const { videos, loading } = this.state;
        return (
            <div className='video-container'>
                <LoadingModal show={loading}/>
                {videos.map(video => 
                    (<div key={video.id} className='video'>
                        <p>{video.name}</p>
                    </div>)
                )}
            </div>
        );
    }

    /*static propTypes = {
    };

    static defaultProps = {
    };*/

}

export default VideoContainer;
