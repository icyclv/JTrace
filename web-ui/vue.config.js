module.exports = {
    lintOnSave: false,
    devServer: {
        proxy: {
            '/api': {
                target: 'http://localhost:8088',
                changeOrigin: true,
            },
        },
    },
};
