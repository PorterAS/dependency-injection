class BusinessRepositoryStub : BusinessRepository {
    override fun getData(identifier: String): String {
        return "Hello World Stubbed!"
    }

}
