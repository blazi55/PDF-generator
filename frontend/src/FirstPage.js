import './App.css';
import React, { useState } from 'react';

import {
    Alert,
    Box,
    Button,
    FormControl,
    InputLabel,
    MenuItem,
    Paper,
    Select,
    Snackbar,
    TextField,
    Typography
} from '@mui/material';

const FONT_SIZES = Array.from(
    { length: 21 },
    (_, i) => i + 10
);

export default function FirstPage() {

    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [fontSizeTitle, setFontSizeTitle] = useState(16);
    const [fontSizeDescription, setFontSizeDescription] = useState(12);

    const [snackbar, setSnackbar] = useState({
        open: false,
        message: '',
        severity: 'success'
    });

    const showSnackbar = (message, severity) => {

        setSnackbar({
            open: true,
            message,
            severity
        });
    };

    const closeSnackbar = () => {

        setSnackbar(prev => ({
            ...prev,
            open: false
        }));
    };

    const onSave = async () => {

        if (!title.trim() || !description.trim()) {

            showSnackbar(
                'Uzupełnij wszystkie pola',
                'warning'
            );

            return;
        }

        const payload = {
            title,
            description,
            fontSizeTitle,
            fontSizeDescription
        };

        try {

            const response = await fetch(
                'http://localhost:8080/pdf/generate',
                {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(payload)
                }
            );

            if (!response.ok) {
                throw new Error();
            }

            showSnackbar(
                'PDF zapisany poprawnie',
                'success'
            );

        } catch (e) {

            showSnackbar(
                'Błąd podczas zapisu PDF',
                'error'
            );
        }
    };

    const onGet = async () => {

        try {

            const response = await fetch(
                'http://localhost:8080/pdf/export'
            );

            if (!response.ok) {
                throw new Error();
            }

            const blob = await response.blob();

            const fileURL =
                window.URL.createObjectURL(blob);

            window.open(fileURL);

            showSnackbar(
                'PDF wygenerowany',
                'success'
            );

        } catch (e) {

            showSnackbar(
                'Błąd podczas pobierania PDF',
                'error'
            );
        }
    };

    return (

        <Box className="app">

            <Box className="container">

                <Paper
                    elevation={0}
                    className="leftCard"
                >

                    <Typography className="mainTitle">
                        Tworzenie PDF
                    </Typography>

                    <Typography className="subtitle">
                        Wprowadź dane dokumentu
                    </Typography>

                    <TextField
                        fullWidth
                        label="Tytuł dokumentu"
                        variant="outlined"
                        value={title}
                        onChange={(e) =>
                            setTitle(e.target.value)
                        }
                        sx={{ mb: 4 }}
                    />

                    <textarea
                        className="descriptionInput"
                        placeholder="Wprowadź opis dokumentu..."
                        value={description}
                        onChange={(e) =>
                            setDescription(e.target.value)
                        }
                    />

                </Paper>

                <Paper
                    elevation={0}
                    className="rightCard"
                >

                    <Typography className="settingsTitle">
                        Ustawienia
                    </Typography>

                    <Box className="buttonSection">

                        <Button
                            variant="contained"
                            className="saveButton"
                            onClick={onSave}
                        >
                            ZATWIERDŹ
                        </Button>

                        <Button
                            variant="outlined"
                            className="downloadButton"
                            onClick={onGet}
                        >
                            POBIERZ PDF
                        </Button>

                    </Box>

                    <Box className="selectSection">

                        <FormControl fullWidth>

                            <InputLabel>
                                Rozmiar tytułu
                            </InputLabel>

                            <Select
                                value={fontSizeTitle}
                                label="Rozmiar tytułu"
                                onChange={(e) =>
                                    setFontSizeTitle(
                                        e.target.value
                                    )
                                }
                            >

                                {FONT_SIZES.map(size => (
                                    <MenuItem
                                        key={size}
                                        value={size}
                                    >
                                        {size}px
                                    </MenuItem>
                                ))}

                            </Select>

                        </FormControl>

                    </Box>

                    <Box className="selectSection">

                        <FormControl fullWidth>

                            <InputLabel>
                                Rozmiar opisu
                            </InputLabel>

                            <Select
                                value={fontSizeDescription}
                                label="Rozmiar opisu"
                                onChange={(e) =>
                                    setFontSizeDescription(
                                        e.target.value
                                    )
                                }
                            >

                                {FONT_SIZES.map(size => (
                                    <MenuItem
                                        key={size}
                                        value={size}
                                    >
                                        {size}px
                                    </MenuItem>
                                ))}

                            </Select>

                        </FormControl>

                    </Box>

                    <Box className="infoBox">

                        <Typography className="infoText">

                            Większa wartość oznacza większą
                            czcionkę w wygenerowanym PDF.

                        </Typography>

                    </Box>

                </Paper>

            </Box>

            <Snackbar
                open={snackbar.open}
                autoHideDuration={3000}
                onClose={closeSnackbar}
                anchorOrigin={{
                    vertical: 'bottom',
                    horizontal: 'right'
                }}
            >

                <Alert
                    onClose={closeSnackbar}
                    severity={snackbar.severity}
                    variant="filled"
                    sx={{
                        width: '100%',
                        borderRadius: '14px',
                        fontWeight: 600
                    }}
                >

                    {snackbar.message}

                </Alert>

            </Snackbar>

        </Box>
    );
}