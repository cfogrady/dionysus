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
            previousIdStack: [],
        };
        this.fetchVideos = this.fetchVideos.bind(this);
        this.unloadVideo = this.unloadVideo.bind(this);
        this.calculatePreviousIdStack = this.calculatePreviousIdStack.bind(this);
        this.fetchPreviousGroup = this.fetchPreviousGroup.bind(this);
    }

    componentDidMount(props) {
        this.fetchVideos(null);
    }

    fetchPreviousGroup() {
        const { previousIdStack } = this.state;
        if(previousIdStack.length > 1) {
            const group = previousIdStack[previousIdStack.length-2];
            this.fetchVideos(group);
        } else {
            this.fetchVideos(null);
        }
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
        const previousIdStack = this.calculatePreviousIdStack(group);
        this.setState({
            loading: true,
            previousIdStack,
        });
    }

    calculatePreviousIdStack(group) {
        let previousIdStack = this.state.previousIdStack;
        if(group != null) {
            const indexOfGroup = previousIdStack.indexOf(group);
            if(indexOfGroup >= 0) {
                //Leaving a group to go back to this one. Delete everything else on the stack.
                previousIdStack = previousIdStack.slice(0, indexOfGroup+1);
            } else {
                //Going to this group for the first time. Add it to the stack
                previousIdStack = [...previousIdStack, group];
            }
        } else {
            //Going to the root level.
            previousIdStack = [];
        }
        return previousIdStack;
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
        const { videos, loading, videoSrc, previousIdStack } = this.state;
        return (
            <div className='video-page-container'>
                {previousIdStack.length > 0 && <button onClick={this.fetchPreviousGroup}>Back</button>}
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
