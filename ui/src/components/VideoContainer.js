import React, { PureComponent } from 'react';
import PropTypes from 'prop-types';
import { GET, fetchVideoSrc } from '../helpers/API';
import LoadingModal from './modals/LoadingModal';
import SimpleModal from './modals/SimpleModal';
import './VideoContainer.css';

class VideoContainer extends PureComponent {
    constructor(props) {
        super(props);
        this.state = {
            loading: true,
            videos: [],
            videoSrc: null,
        };
        this.fetchVideos = this.fetchVideos.bind(this);
        this.unloadVideo = this.unloadVideo.bind(this);
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
                videos: data || [],
            });
        });
        this.setState({
            loading: true,
        });
    }

    playElement(video) {
        return _ => {
            if(video.groupContainer) {
                this.fetchVideos(video.id);
            } else {
                const videoSrc = fetchVideoSrc(video.id);
                this.setState({
                    videoSrc,
                })
            }
        }
    }

    unloadVideo() {
        this.setState({
            videoSrc: null,
        });
    }

    render() {
        const { videos, loading, videoSrc } = this.state;
        return (
            <div className='video-container'>
                <SimpleModal show={videoSrc != null}>
                    <div className="video-element-container">
                        <video width="720" autoplay="autoplay" controls key={videoSrc}>
                            <source src={videoSrc} type="video/mp4"/>
                        </video>
                        <button onClick={this.unloadVideo}>Back</button>
                    </div>
                </SimpleModal>
                <LoadingModal show={loading}/>
                {videos.map(video => 
                    (<div key={video.id} onClick={this.playElement(video)} className='video'>
                        <p>{video.name}</p>
                    </div>)
                )}
            </div>
        );
    }

    static propTypes = {
        jwt: PropTypes.string.isRequired,
    };

    static defaultProps = {
    };

}

export default VideoContainer;
