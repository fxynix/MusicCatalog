import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import {Table, Button, Space, Modal, Form, Input, Select, message} from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import axios from 'axios';

const { Column } = Table;
const { Option } = Select;

const PlaylistList = () => {
  const [playlists, setPlaylists] = useState([]);
  const [users, setUsers] = useState([]);
  const [allTracks, setAllTracks] = useState([]);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingPlaylist, setEditingPlaylist] = useState(null);
  const [form] = Form.useForm();
  const { search } = useLocation();
  const queryParams = new URLSearchParams(search);
  const authorId = queryParams.get('authorId');

  useEffect(() => {
    fetchPlaylists();
    fetchUsers();
    fetchAllTracks();
  }, [authorId]);

  const fetchPlaylists = async () => {
    try {
      const url = authorId
          ? `${process.env.REACT_APP_API_URL}/playlists?authorId=${authorId}`
          : `${process.env.REACT_APP_API_URL}/playlists/all`;

      const response = await axios.get(url);
      setPlaylists(response.data);
    } catch (error) {
      if (authorId) {
        message.error('Not found author\'s playlists');
        setPlaylists(null);
      } else {
        message.error('Failed to fetch playlists');
      }
    }
  };

  const fetchUsers = async () => {
    try {
      const response = await axios.get(`${process.env.REACT_APP_API_URL}/users/all`);
      setUsers(response.data);
    } catch (error) {
      message.error('Failed to fetch users');
    }
  };

  const fetchAllTracks = async () => {
    try {
      const response = await axios.get(`${process.env.REACT_APP_API_URL}/tracks/all`);
      setAllTracks(response.data);
    } catch (error) {
      message.error('Failed to fetch tracks');
    }
  };

  const showModal = (playlist = null) => {
    setEditingPlaylist(playlist);
    form.resetFields();
    if (playlist) {
      form.setFieldsValue({
        name: playlist.name,
        authorId: users.find(u => u.name === playlist.author)?.id,
        tracksIds: allTracks
            .filter(track => playlist.tracks?.includes(track.name))
            .map(track => track.id) || []
      });
    } else {
      form.setFieldsValue({
        name: '',
        authorId: undefined,
        tracksIds: []
      });
    }
    setIsModalVisible(true);
  };

  const handleSubmit = async (values) => {
    try {
      const requestData = {
        name: values.name,
        authorId: values.authorId,
        tracksIds: values.tracksIds || []
      };

      console.log('Sending data:', requestData);

      if (editingPlaylist) {
        await axios.patch(
            `${process.env.REACT_APP_API_URL}/playlists/${editingPlaylist.id}`,
            requestData
        );
        message.success('Playlist updated successfully');
      } else {
        await axios.post(
            `${process.env.REACT_APP_API_URL}/playlists`,
            requestData
        );
        message.success('Playlist created successfully');
      }

      fetchPlaylists();
      setIsModalVisible(false);
    } catch (error) {
      console.error('Error:', error.response?.data);

      if (error.response?.status === 400) {
        const errorMessages = Object.entries(error.response.data)
            .flatMap(([field, errors]) =>
                Array.isArray(errors)
                    ? errors.map(e => `${field}: ${e}`)
                    : `${field}: ${errors}`
            )
            .join('\n');

        message.error({
          content: <div style={{ whiteSpace: 'pre-line' }}>{errorMessages}</div>,
          duration: 5
        });
      } else {
        message.error(error.response?.data?.message || 'Failed to save playlist');
      }
    }
  };

  const handleDelete = async (id) => {
    Modal.confirm({
      title: 'Delete Playlist',
      content: 'Are you sure you want to delete this playlist?',
      okText: 'Delete',
      okType: 'danger',
      cancelText: 'Cancel',
      onOk: async () => {
        try {
          await axios.delete(`${process.env.REACT_APP_API_URL}/playlists/${id}`);
          message.success('Playlist deleted successfully');
          fetchPlaylists();
        } catch (error) {
          message.error('Failed to delete playlist');
        }
      }
    });
  };

  return (
      <div className="container">
        <div className="actions">
          <Button type="primary" icon={<PlusOutlined />} onClick={() => showModal()}>
            Add Playlist
          </Button>
        </div>

        <Table dataSource={playlists} rowKey="id">
          <Column title="Name" dataIndex="name" key="name" />
          <Column title="Author" dataIndex="author" key="author" />
          <Column
              title="Tracks"
              key="tracks"
              render={(_, playlist) => playlist.tracks?.length || 0}
          />
          <Column
              title="Action"
              key="action"
              render={(_, playlist) => (
                  <Space size="middle">
                    <Button
                        type="link"
                        icon={<EditOutlined />}
                        onClick={() => showModal(playlist)}
                    />
                    <Button
                        type="link"
                        icon={<DeleteOutlined />}
                        onClick={() => handleDelete(playlist.id)}
                        danger
                    />
                  </Space>
              )}
          />
        </Table>

        <Modal
            title={editingPlaylist ? "Edit Playlist" : "Add Playlist"}
            open={isModalVisible}
            onOk={() => form.submit()}
            onCancel={() => setIsModalVisible(false)}
            width={700}
        >
          <Form form={form} onFinish={handleSubmit} layout="vertical">
            <Form.Item
                name="name"
                label="Playlist Name"
                required={true}
            >
              <Input placeholder="Enter playlist name" />
            </Form.Item>

            <Form.Item
                name="authorId"
                label="Author"
                rules={[
                  { required: true, message: 'Please select author!' },
                  { type: 'number', min: 1, message: 'Author ID must be positive' }
                ]}
            >
              <Select
                  showSearch
                  optionFilterProp="children"
                  placeholder="Select author"
                  allowClear={false}
              >
                {users.map(user => (
                    <Option key={user.id} value={user.id}>
                      {user.name}
                    </Option>
                ))}
              </Select>
            </Form.Item>

            <Form.Item
                name="tracksIds"
                label="Tracks"
                initialValue={[]}
            >
              <Select
                  mode="multiple"
                  showSearch
                  optionFilterProp="children"
                  placeholder="Select tracks"
                  allowClear
              >
                {allTracks.map(track => (
                    <Option key={track.id} value={track.id}>
                      {track.name}
                    </Option>
                ))}
              </Select>
            </Form.Item>
          </Form>
        </Modal>
      </div>
  );
};

export default PlaylistList;